package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

import org.w3c.dom.Document;
import java.util.Arrays;

public class AddNewLineController implements Initializable {


    public ChoiceBox typeChooser;
    public AnchorPane attributeAncor;
    private PrimaryController primaryController;
    private TreeView<String> primaryTreeView;


    /**
     * Frissíti az attribútummezőket a megadott típus alapján.
     *
     * @param type Az attribútummezők frissítéséhez használt típus.
     */
    private void updateAttributeFields(String type) {
        attributeAncor.getChildren().clear(); // Clear the previous fields

        switch (type) {
            case "constant":
                addTextField("name",0);
                addTextField("value",40);
                break;
            case "linking":
                addTextField("Source",0);
                addTextField("Target",40);
                break;
            case "conversion":
                addTextField("Type",0);
                break;
            default:
                // No extra content for other types
                break;
        }
    }

    /**
     * Hozzáad egy szövegmezőt a megadott címkével és elrendezési Y-koordinátával.
     *
     * @param label     A szövegmezőhöz tartozó címke.
     * @param layoutY   Az elrendezési Y-koordináta, ahol a szövegmezőt elhelyezzük.
     */
    private void addTextField(String label, int layoutY) {
        TextField textField = new TextField();
        textField.setPromptText(label);
        textField.setLayoutY(layoutY);
        attributeAncor.getChildren().add(textField);
    }

    /**
     * Visszalépés eseménykezelő, amely bezárja az aktuális ablakot.
     *
     * @param actionEvent Az esemény, amely aktiválja a visszalépési műveletet.
     */
    public void onBack(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Ellenőrzi, hogy a megadott Pane attribútumai nem nullák.
     *
     * @param attributeAncor A Pane objektum, amelynek attribútumait ellenőrizzük.
     * @return {@code true}, ha az összes attribútum nem null; {@code false} egyébként.
     */
    public boolean checkChildrenNotNull(Pane attributeAncor) {
        for (Node child : attributeAncor.getChildren()) {
            if (child instanceof TextField) {
                TextField textField = (TextField) child;
                String text = textField.getText();
                if (text.length() != 0) {
                    System.out.println("Text field value: " + text); //TODO
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Gyermek mentése eseménykezelő, amely hozzáad egy új XML címkét a kiválasztott elemhez gyermekként.
     * Az inputok alapján az XML címke összeállítása történik.
     *
     * @param actionEvent Az esemény, amely aktiválja a gyermek mentési műveletet.
     */
    public void onSaveChild(ActionEvent actionEvent) {
        // Ellenőrizzük, hogy van-e kiválasztott elem a TreeView-ban
        TreeItem<String> selectedItem = primaryTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            // Összeállítjuk az új XML tag-et az inputok alapján
            String newXmlTag = assembleXmlTag();

            // Ha sikerült az XML tag összeállítása
            if (!newXmlTag.isEmpty()) {
                // Hozzáadjuk az új XML tag-et a kiválasztott elemhez gyerekként
                selectedItem.getChildren().add(new TreeItem<>(newXmlTag));
                primaryController.convertTreeViewToXml(); // Frissítjük a XML előnézetet
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.close();
            } else {
                showAlert("Error!", "Please choose a type.");
            }
        } else {
            showAlert("Error!", "Nincs kiválasztott mező!");
        }
    }


    /**
     * Összeállítja az XML címkét az inputok alapján.
     *
     * @return Az összeállított XML címke.
     */
    private String assembleXmlTag() {
        StringBuilder xmlTagBuilder = new StringBuilder();
        if(typeChooser.getValue() != null) {
            // Összeállítjuk az XML tag-et az inputok alapján
            xmlTagBuilder.append(typeChooser.getValue()).append(" ");

            // Hozzáadjuk az attribútumokat az inputok alapján
            for (Node child : attributeAncor.getChildren()) {
                if (child instanceof TextField) {
                    TextField textField = (TextField) child;
                    String attribute = textField.getPromptText();
                    String value = textField.getText();
                    if (!value.isEmpty()) {
                        xmlTagBuilder.append(attribute).append("=\"").append(value).append("\" ");
                    }
                }
            }
        }
        return xmlTagBuilder.toString();
    }

    /**
     * Testvér mentése eseménykezelő, amely hozzáad egy új XML címkét a kiválasztott elem testvéréhez.
     * Az inputok alapján az XML címke összeállítása történik.
     *
     * @param actionEvent Az esemény, amely aktiválja a testvér mentési műveletet.
     */
    public void onSaveBrother(ActionEvent actionEvent) {
        // Ellenőrizzük, hogy van-e kiválasztott elem a TreeView-ban
        TreeItem<String> selectedItem = primaryTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.getParent() != null) {
            // Összeállítjuk az új XML tag-et az inputok alapján
            String newXmlTag = assembleXmlTag();

            // Ha sikerült az XML tag összeállítása
            if (!newXmlTag.isEmpty()) {
                // Hozzáadjuk az új XML tag-et a kiválasztott elem testvérének
                TreeItem<String> parent = selectedItem.getParent();
                int selectedIndex = parent.getChildren().indexOf(selectedItem);
                parent.getChildren().add(selectedIndex + 1, new TreeItem<>(newXmlTag));
                primaryController.convertTreeViewToXml(); // Frissítjük a XML előnézetet
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.close();
            } else {
                showAlert("Error!", "Please choose a type.");
            }
        } else {
            showAlert("Error!", "No item selected in TreeView or selected item has no parent.");
        }
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
     * Beállítja a fofa nézetet a megadott objektumra.
     *
     * @param primaryTreeView A fofa nézetet reprezentáló objektum.
     */
    public void setPrimaryTreeView(TreeView<String> primaryTreeView) {
        this.primaryTreeView = primaryTreeView;
    }

    /**
     * Inicializálja a kontrollert.
     *
     * @param url             A kezdetiizálás URL-je vagy {@code null}, ha az ismeretlen.
     * @param resourceBundle A kezdetiizálás erőforrás-kötege vagy {@code null}, ha az ismeretlen.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeChooser.getItems().addAll("resources", "constant", "linking", "binding", "conversions", "conversion");
        typeChooser.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> updateAttributeFields(newValue.toString()));
    }
}
