package org.example;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.*;
import org.jetbrains.annotations.NotNull;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.xml.sax.SAXException;
import java.util.Optional;


/**
 * A PrimaryController osztály felelős a főbb funkciók kezeléséért
 * a grafikus felhasználói felületen.
 */
public class PrimaryController implements Initializable {
    public Document xmldoc;
    public TextArea xmlPreviewTArea;
    public TreeView<String> primaryTreeView; // Specify the generic type for TreeView
    public Label documentPathLabel;
    public Label docName;
    private final TreeViewController tv = new TreeViewController(this);

    /**
     * Betölti a kiválasztott XML fájlt a grafikus felületre.
     * @param event Az esemény, amely a metódus meghívásához vezetett.
     * @return Igaz, ha sikeres volt a betöltés, egyébként hamis.
     */
    @FXML
    private boolean importXml(@NotNull ActionEvent event) {
        // Esemény forrásának meghatározása és az azt tartalmazó színpad lekérése
        javafx.scene.Node source = (javafx.scene.Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        // Fájl kiválasztó dialógusablak inicializálása
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select XML File");

        // Felhasználó által kiválasztott fájl lekérése
        File selectedFile = fileChooser.showOpenDialog(stage);

        // Ha nincs kiválasztva fájl, visszatérés hamissal
        if (selectedFile == null) {
            return false;
        }

        // Kiválasztott fájl elérési útjának kiírása a konzolra
        System.out.println("Selected file: " + selectedFile.getAbsolutePath());

        // A kiválasztott fájl megnyitása és feldolgozása
        return open(selectedFile);
    }

    /**
     * Megnyitja a kiválasztott fájlt.
     * Ha sikerült megnyitni, továbbhívja a goodFile metódust,
     * egyébként meghívja a badFileWarn metódust.
     *
     * @param selectedFile A kiválasztott fájl, amelyet meg kell nyitni.
     * @return Igaz, ha sikeres volt a fájl megnyitása és feldolgozása, egyébként hamis.
     */
    private boolean open(File selectedFile){
        try {
           return goodFile(selectedFile);
        } catch (Exception e) {
           return badFileWarn();
        }
    }


    /**
     * Ellenőrzi, hogy a kiválasztott XML fájl helyes formátumú-e,
     * majd betölti a grafikus felületre és megjeleníti a tartalmát.
     * @param selectedFile A kiválasztott XML fájl.
     * @return Igaz, ha sikeres volt a fájl megnyitása, egyébként hamis.
     * @throws IOException Ha hiba történik az olvasás közben.
     * @throws SAXException Ha hiba történik a SAX parsing során.
     * @throws TransformerException Ha hiba történik a transformer során.
     * @throws ParserConfigurationException Ha hiba történik a parser konfigurálása során.
     */
    private boolean goodFile(File selectedFile) throws IOException, SAXException, TransformerException, ParserConfigurationException {
        // XML fájl feldolgozása és megjelenítése
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder(); // DocumentBuilder létrehozása a DocumentBuilderFactory segítségével
        FileInputStream fileInputStream = new FileInputStream(selectedFile); // Fájl beolvasása
        Document document = builder.parse(fileInputStream); // XML dokumentum létrehozása a beolvasott fájlból
        fileInputStream.close(); // Beolvasás lezárása

        // XML dokumentum beállítása az osztályváltozóban tárolásra és megjelenítésre
        this.xmldoc = document; // Az XML dokumentum eltárolása az osztályváltozóban
        xmlPreviewTArea.setText(getStringFromDocument(this.xmldoc)); // XML tartalom megjelenítése a TextArea-ban

        // Fa struktúra létrehozása az XML dokumentum alapján
        tv.createTreeView(); // Fa struktúra létrehozása a TreeView objektum segítségével

        // Fájl elérési útjának és nevének megjelenítése
        documentPathLabel.setText(selectedFile.getAbsolutePath()); // Fájl elérési útvonalának megjelenítése
        docName.setText(selectedFile.getName()); // Fájl nevének megjelenítése

        return true; // Sikeres feldolgozás esetén igaz érték visszaadása
    }

    /**
     * Figyelmeztetés megjelenítése, hogy a kiválasztott fájl nem megfelelő formátumú.
     * A felhasználót tájékoztatja arról, hogy az adott fájl nem XML formátumú.
     *
     * @return Hamis értékkel tér vissza, mivel a fájl nem megfelelő formátumú.
     */
    private boolean badFileWarn(){
        System.out.println("File import error");
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("Ez nem egy xml fájl!");
        alert.showAndWait();
        return false;
    }

    /**
     * Exportálja az XML tartalmat a felhasználó által kiválasztott helyre.
     *
     * @param event Az esemény, amely a metódus meghívásához vezetett.
     * @return Igaz, ha sikeres volt az exportálás, egyébként hamis.
     */
    @FXML
    private boolean exportXml(@NotNull ActionEvent event) {
        Node source = (Node) event.getSource(); // Az esemény forrásának meghatározása
        Stage stage = (Stage) source.getScene().getWindow(); // Az eseményt tartalmazó színpad lekérése

        // Fájl kiválasztó dialógusablak inicializálása és megjelenítése
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export XML File"); // Dialógusablak címe
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"); // Fájlfiltet beállítása XML fájlokra
        fileChooser.getExtensionFilters().add(extFilter); // Fájlfiltet hozzáadása a kiválasztóhoz
        File selectedFile = fileChooser.showSaveDialog(stage); // Mentési dialógusablak megjelenítése

        // Ha nem választottak ki fájlt, visszatérés igazzal
        if (selectedFile == null) {
            return true;
        }

        try {
            // Az XML tartalom megírása a kiválasztott fájlba
            return writeToFile(selectedFile);
        } catch (Exception e) {
            // Ha hiba történt a fájlírás közben, figyelmeztetés megjelenítése és visszatérés hamissal
            return exportFail();
        }
    }

    /**
     * Az XML tartalmat írja a kiválasztott fájlba.
     *
     * @param selectedFile A kiválasztott fájl, amelybe az XML tartalmat írni kell.
     * @return Igaz, ha sikeresen sikerült az írás, egyébként hamis.
     * @throws IOException Ha hiba történik az írás során.
     */
    private boolean writeToFile(File selectedFile) throws IOException {
        // Az XML tartalom lekérése a TextArea-ból
        String xmlContent = xmlPreviewTArea.getText();

        // Író inicializálása a kiválasztott fájlra
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
            // Az XML tartalom írása a kiválasztott fájlba
            writer.write(xmlContent);
        }
        // Visszatérés igazzal, ha sikeres volt az írás
        return true;
    }

    /**
     * Figyelmeztetés megjelenítése, ha hiba történik az XML exportálása során.
     *
     * @return Hamis értékkel tér vissza, mivel nem sikerült az exportálás.
     */
    private boolean exportFail(){
        // Hibauzenet kiírása a konzolra
        System.out.println("File export error");

        // Figyelmeztető párbeszédablak megjelenítése
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("Nem sikerült az export!");
        alert.showAndWait();

        // Hamis érték visszaadása, mivel nem sikerült az exportálás
        return false;
    }



    /**
     * Átalakítja az XML dokumentumot szöveges formátumba.
     *
     * @param doc Az XML dokumentum, amelyet át kell alakítani.
     * @return Az XML dokumentum szöveges reprezentációja.
     * @throws TransformerException Ha hiba történik az átalakítás során.
     */
    public static String getStringFromDocument(Document doc) throws TransformerException {
        // DOM forrás inicializálása
        DOMSource domSource = new DOMSource(doc);

        // Író inicializálása
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);

        // TransformerFactory létrehozása
        TransformerFactory tf = TransformerFactory.newInstance();

        // Transformer létrehozása
        Transformer transformer = tf.newTransformer();

        // XML dokumentum átalakítása szöveges formátummá
        transformer.transform(domSource, result);

        // Szöveges formátum visszaadása
        return writer.toString();
    }


    /**
     * Inicializálja a kontrollert az FXML fájl betöltésekor.
     * Beállítja a kezdeti szöveget a xmlPreviewTArea TextArea-ban
     * és letiltja annak szerkesztését.
     *
     * @param url Az inicializáció URL-je, ami nem használt ebben az esetben.
     * @param resourceBundle Az inicializációhoz tartozó nyelvi erőforrások, ami nem használt ebben az esetben.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Kezdeti szöveg beállítása a TextArea-ban
        this.xmlPreviewTArea.setText("Import a document please...");
        // Szerkesztés letiltása a TextArea-ban
        this.xmlPreviewTArea.setEditable(false);
    }


    /**
     * Az "Új sor hozzáadása" funkció megvalósítása.
     * Betölti az "addNewLine.fxml" fájlt, beállítja a megfelelő referenciákat
     * a kontrollerek között, majd megjeleníti az új ablakot.
     *
     * @param actionEvent Az esemény, amely a funkció meghívásához vezetett.
     */
    public void onAdd(ActionEvent actionEvent) {
        try {
            // Betöltjük az FXML fájlt
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("addNewLine.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 294, 444);

            // Lekérjük az AddNewLineController példányt
            AddNewLineController controller = fxmlLoader.getController();

            // Beállítjuk a PrimaryController referenciát
            controller.setPrimaryController(this);

            // Beállítjuk a primaryTreeView referenciát
            controller.setPrimaryTreeView(primaryTreeView);

            // Új ablak megjelenítése
            Stage stage = new Stage();
            stage.setTitle("Add new line");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            // Ha hiba történik az FXML fájl betöltésekor, RuntimeException dobása
            throw new RuntimeException(e);
        }
    }

    /**
     * Az elem törlését végzi el a fa struktúrából.
     * Ellenőrzi, hogy van-e kiválasztott elem, majd megjeleníti egy megerősítő párbeszédablakot
     * a törlés megerősítéséhez. Ha a felhasználó megerősíti a törlést,
     * az adott elemet eltávolítja a fa struktúrából, és frissíti az XML-t.
     *
     * @param actionEvent Az esemény, amely a törlési funkció meghívásához vezetett.
     */
    @FXML
    private void onDelete(ActionEvent actionEvent) {
        // Kiválasztott elem lekérése a fa struktúrából
        TreeItem<String> selectedItem = primaryTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            // Ellenőrzés, hogy a felhasználó törölni szeretné-e az elemet
            boolean confirmation = showConfirmationDialog("Delete Confirmation", "Are you sure you want to delete this item?");
            if (confirmation) {
                // Az elem törlése a fa struktúrából
                selectedItem.getParent().getChildren().remove(selectedItem);
                // XML frissítése az elem törlése után
                convertTreeViewToXml();
            }
        } else {
            // Figyelmeztetés megjelenítése, ha nincs kiválasztott elem
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Hiba!");
            alert.setHeaderText(null);
            alert.setContentText("Nincs kiválasztott mező!");
            alert.showAndWait();
        }
    }

    /**
     * Megerősítő párbeszédablak megjelenítése a felhasználó számára.
     * A felhasználó megerősítheti vagy megszakíthatja az adott műveletet.
     *
     * @param title A megerősítő párbeszédablak címe.
     * @param message A megerősítő párbeszédablak üzenete.
     * @return Igaz, ha a felhasználó megerősítette az adott műveletet, egyébként hamis.
     */
    private boolean showConfirmationDialog(String title, String message) {
        // Megerősítő párbeszédablak inicializálása
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // A felhasználó válaszának lekérése
        Optional<ButtonType> result = alert.showAndWait();

        // Az eredmény értéke alapján igaz vagy hamis visszatérés
        return result.isPresent() && result.get() == ButtonType.OK;
    }


    /**
     * Az "Elem szerkesztése" funkció megvalósítása.
     * Megjeleníti az "editLine.fxml" fájlban definiált szerkesztő ablakot,
     * amely lehetővé teszi egy kiválasztott elem szerkesztését.
     *
     * @param actionEvent Az esemény, amely a szerkesztési funkció meghívásához vezetett.
     */
    public void onEdit(ActionEvent actionEvent) {
        // Az új ablak méreteinek beállítása
        final int newSceneWidth = 600;
        final int newSceneHeight = 600;

        // Kiválasztott elem lekérése a fa struktúrából
        TreeItem<String> selectedItem = primaryTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            try {
                // Az FXML fájl betöltése
                FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("editLine.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), newSceneWidth, newSceneHeight);

                // EditLineController példány lekérése és beállítása
                EditLineController controller = fxmlLoader.getController();
                controller.setPrimaryController(this); // PrimaryController referenciát átadása
                controller.setSelectedItem(selectedItem); // Kiválasztott elem beállítása

                // Szerkesztő ablak megjelenítése
                Stage stage = new Stage();
                stage.setTitle("Edit line");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                // Ha hiba történik az FXML fájl betöltésekor, RuntimeException dobása
                throw new RuntimeException(e);
            }
        } else {
            // Figyelmeztetés megjelenítése, ha nincs kiválasztott elem
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Hiba!");
            alert.setHeaderText(null);
            alert.setContentText("Nincs kiválasztott mező!");

            alert.showAndWait();
        }
    }

    /**
     * A fa struktúrában lévő adatok átalakítása XML formátumba, majd az eredmény megjelenítése.
     * Ellenőrzi, hogy van-e gyökér elem a fa struktúrában, és ha igen, átalakítja az összes alárendelt elemet XML formátumba.
     * Az XML formátumot a `xmlPreviewTArea` TextArea-ban jeleníti meg.
     */
    public void convertTreeViewToXml() {
        // StringBuilder létrehozása az XML tartalom tárolására
        StringBuilder xmlStringBuilder = new StringBuilder();
        // Gyökér elem lekérése a fa struktúrából
        TreeItem<String> rootItem = primaryTreeView.getRoot();
        if (rootItem != null) {
            // Gyökér elem átalakítása XML formátumba, majd hozzáadása az XML StringBuilder-hez
            xmlStringBuilder.append(convertTreeItemToXml(rootItem, 0));
            // Az XML tartalom megjelenítése a TextArea-ban
            xmlPreviewTArea.setText(xmlStringBuilder.toString());
        }
    }


    // Recursive function to convert TreeItem to XML format
    private String convertTreeItemToXml(TreeItem<String> item, int depth) {
        StringBuilder xmlString = new StringBuilder();
        String indentation = "\t".repeat(depth); // indentation for child elements

        // Add opening tag with attributes if available
        String itemValue = item.getValue();
        String attributes = "";
        if (itemValue.contains("=")) {
            String[] parts = itemValue.split(" ");
            itemValue = parts[0]; // Get the tag name
            attributes = " " + String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
        }
        xmlString.append(indentation).append("<").append(itemValue).append(attributes);

        // Check if the item has children
        if (!item.getChildren().isEmpty()) {
            xmlString.append(">\n");

            // Add children recursively
            for (TreeItem<String> child : item.getChildren()) {
                xmlString.append(convertTreeItemToXml(child, depth + 1));
            }

            // Add closing tag
            xmlString.append(indentation).append("</").append(itemValue).append(">\n");
        } else {
            // If the item has no children, close the tag differently
            xmlString.append("/>\n");
        }

        return xmlString.toString();
    }

    /**
     * Az "Súgó" funkció megvalósítása.
     * Megjeleníti a "help.fxml" fájlban definiált súgó ablakot, amely tartalmazza az alkalmazás használati útmutatóját.
     *
     * @param actionEvent Az esemény, amely a súgó funkció meghívásához vezetett.
     * @throws IOException Ha hiba történik az FXML fájl betöltésekor.
     */
    @FXML
    public void onHelp(ActionEvent actionEvent) throws IOException {
        // Az új ablak méreteinek beállítása
        final int newSceneWidth = 600;
        final int newSceneHeight = 600;

        // FXMLLoader inicializálása és az FXML fájl betöltése
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("help.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), newSceneWidth, newSceneHeight);

        // helpController példány létrehozása és beállítása
        helpController controller = fxmlLoader.getController();
        controller.setPrimaryController(this); // PrimaryController referenciát átadása

        // Az új ablak létrehozása és megjelenítése
        Stage stage = new Stage();
        stage.setTitle("Help");
        stage.setScene(scene);
        stage.show();
    }


}
