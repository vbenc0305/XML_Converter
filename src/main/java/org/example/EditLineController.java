package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

/**
 * Az XML dokumentum sorainak módosításáért felelos kontroller osztály.
 */
public class EditLineController {
    @FXML
    public TextField textFieldType;
    @FXML
    public AnchorPane anchorForAttributes;
    private PrimaryController primaryController;
    private TreeItem<String> selectedItem;


    /**
     * Beállítja a szoveget a kiválasztott elem alapján.
     * @param selectedItemString A kiválasztott elem szövege.
     */
    public void setText(String selectedItemString) {
        String processedString = preprocessString(selectedItemString);
        String extractedText = extractText(processedString);
        showEditedText(extractedText);
    }

    /**
     * A kiválasztott szoveg elofeldolgozása.
     *
     * @param selectedItemString A kiválasztott elem szovege.
     * @return Az előfeldolgozott szoveg, amely eltávolítja az elso sortorést, ha van ilyen.
     */
    private String preprocessString(String selectedItemString) {
        int newlineIndex = selectedItemString.indexOf('\n');
        if (newlineIndex != -1) {
            return selectedItemString.substring(0, newlineIndex);
        } else {
            return selectedItemString;
        }
    }

    /**
     * Kinyeri a szöveget a feldolgozott szovegből, ha van HTML formázás.
     *
     * @param processedString A feldolgozott szoveg, amelybol a szoveg kinyerése torténik.
     * @return Az extrahált szoveg, ha van HTML formázás, külonben a feldolgozott szoveg.
     */
    private String extractText(String processedString) {
        int startIndex = processedString.indexOf('<');
        if (startIndex != -1) {
            int endIndex = processedString.indexOf('>');
            if (endIndex != -1) {
                return processedString.substring(startIndex + 1, endIndex);
            }
        }
        return processedString;
    }

    /**
     * Megjeleníti a szerkesztett szoveget, ha van ilyen.
     *
     * @param text A szerkesztett szoveg, amely megjelenítésre kerul.
     */
    private void showEditedText(String text) {
        if (text != null && !text.isEmpty()) {
            String[] parts = parse(text);
            if (parts.length > 0) {
                display(parts);
            }
        }
    }

    /**
     * Feldarabolja a szöveget szóközök mentén.
     *
     * @param text A szöveg, amelyet feldarabol.
     * @return Egy tömb, amely a szavakat tartalmazza a szövegből.
     */
    private String[] parse(String text) {
        return text.split("\\s+");
    }

    /**
     * Megjeleníti a szöveg részeit, egyes részeket beállítva egy TextField-be.
     *
     * @param parts A szövegrészektől alkotott tömb, amelyek megjelenítésre kerülnek.
     */
    private void display(String[] parts) {
        textFieldType.setText(parts[0]);
        double yPos = 0;
        for (int i = 1; i < parts.length; i++) {
            TextField attributeField = new TextField(parts[i]);
            attributeField.setLayoutX(26);
            attributeField.setLayoutY(yPos);
            yPos += 30;
            anchorForAttributes.getChildren().add(attributeField);
        }
    }

    /**
     * Mentés eseménykezelő, amely elmenti a módosított értékeket, konvertálja a fastruktúrát XML-be,
     * majd bezárja az aktuális ablakot.
     *
     * @param actionEvent Az esemény, amely aktiválta a mentési műveletet.
     */
    public void onSave(ActionEvent actionEvent) {
        if (this.selectedItem == null || this.primaryController == null) return;

        StringBuilder newValue = new StringBuilder(textFieldType.getText());
        getAttributes(newValue);

        this.selectedItem.setValue(newValue.toString());
        this.primaryController.convertTreeViewToXml();

        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.close();
    }

    /**
     * Az attribútumokat gyűjti össze a megadott StringBuilder-be.
     *
     * @param newValue A StringBuilder objektum, amelybe az attribútumok kerülnek.
     */
    private void getAttributes(StringBuilder newValue){
        for (Node child : anchorForAttributes.getChildren()) {
            if (!(child instanceof TextField)) {
                continue;
            }
            String attributeText = ((TextField) child).getText().trim();
            if (!attributeText.isEmpty()) {
                newValue.append(" ").append(attributeText);
            }
        }
    }

    /**
     * Visszalépés eseménykezelő, amely bezárja az aktuális ablakot.
     *
     * @param actionEvent Az esemény, amely aktiválta a visszalépési műveletet.
     */
    public void onBack(ActionEvent actionEvent) {
        ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();
    }

    /**
     * Beállítja a fővezérlőt a megadott PrimaryController objektumra.
     *
     * @param primaryController A fovezerlot reprezentáló objektum.
     */
    public void setPrimaryController(PrimaryController primaryController) {
        this.primaryController = primaryController;
    }

    /**
     * Beállítja a kiválasztott elemet és frissíti a szöveget az elem értékével.
     *
     * @param selectedItem Az új kiválasztott elem {@code TreeItem<String>} formátumban.
     */
    public void setSelectedItem(TreeItem<String> selectedItem) {
        this.selectedItem = selectedItem;
        setText(selectedItem.getValue());
    }
}

