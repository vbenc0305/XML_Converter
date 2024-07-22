package org.example;

import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
/**
 * A `TreeViewController` osztály felelős a fa nézet létrehozásáért az XML dokumentum megjelenítéséhez.
 * Az osztály létrehoz egy fa struktúrát az XML dokumentum elemeiből és attribútumaiból,
 * majd megjeleníti ezt a fa struktúrát a felhasználói felületen.
 */
public class TreeViewController {

    /**
     * A `PrimaryController` osztály referenciája, amely tartalmazza az XML dokumentumot és a felhasználói felületet.
     */
    private final PrimaryController pc;

    /**
     * Konstruktor, amely inicializálja a `TreeViewController` osztályt egy `PrimaryController` példánnyal.
     * @param pc A `PrimaryController` példány, amely tartalmazza az XML dokumentumot és a felhasználói felületet.
     */
    public TreeViewController(PrimaryController pc) {
        this.pc = pc;
    }

    /**
     * Létrehoz egy fa struktúrát az XML dokumentum elemeiből és attribútumaiból,
     * majd megjeleníti ezt a fa struktúrát a felhasználói felületen.
     */
    protected void createTreeView() {
        if (pc.xmldoc != null) {
            // Gyökér fa elem létrehozása az XML deklarációval
            TreeItem<String> rootItem = new TreeItem<>("?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?");
            // Fa struktúra létrehozása az XML dokumentum gyökér eleméből
            createTree(pc.xmldoc.getDocumentElement(), rootItem);
            // Gyökér fa elem beállítása a fa nézet gyökerelemének
            pc.primaryTreeView.setRoot(rootItem);
            // Gyökér fa elem kinyitása
            rootItem.setExpanded(true);
        }
    }

    /**
     * Rekurzív metódus, amely végigmegy az XML dokumentum elemein és attribútumain,
     * és létrehoz egy fa struktúrát a megjelenítéshez.
     * @param node Az aktuális XML csomópont.
     * @param parentItem A szülő fa elem a fa struktúrában.
     */
    protected void createTree(org.w3c.dom.Node node, TreeItem<String> parentItem) {
        if (node instanceof Element) {
            Element element = (Element) node;
            // Az XML elem attribútumaival létrehozott fa elem létrehozása
            TreeItem<String> currentItem = createItemWithAttributes(element);
            // Az aktuális fa elem hozzáadása a szülő fa elemhez
            parentItem.getChildren().add(currentItem);

            // Az XML elem gyerekeinek feldolgozása
            NodeList nodeList = element.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                org.w3c.dom.Node child = nodeList.item(i);
                // Ha a gyerek csomópont XML elem, akkor rekurzívan hozzunk létre fa elemeket
                if (child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    createTree(child, currentItem);
                }
            }
        }
    }

    /**
     * Létrehoz egy fa elemet az XML elem attribútumaival együtt.
     * @param element Az XML elem, amelyből a fa elem létre lesz hozva.
     * @return Az újonnan létrehozott fa elem az XML elem attribútumaival.
     */
    @Contract("_ -> new")
    private @NotNull TreeItem<String> createItemWithAttributes(@NotNull Element element) {
        StringBuilder itemText = new StringBuilder(element.getNodeName());
        NamedNodeMap attributes = element.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                org.w3c.dom.Node attribute = attributes.item(i);
                // Az attribútum nevének és értékének hozzáadása az elem szövegéhez
                itemText.append(" ").append(attribute.getNodeName()).append("=").append("\"").append(attribute.getNodeValue()).append("\"");
            }
        }
        // Az elem nevével és attribútumaival létrehozott fa elem létrehozása és visszaadása
        return new TreeItem<>(itemText.toString());
    }
}
