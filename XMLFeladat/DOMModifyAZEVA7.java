
import java.io.*;
import java.text.ParseException;
import java.time.*;
import java.time.format.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.w3c.dom.*;
import org.w3c.dom.traversal.*;
import org.xml.sax.*;

public class DOMModifyAZEVA7 {
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException,
            XPathExpressionException, DOMException, ParseException {
        // XML fájl beolvasása
        File xml = new File("XMLAZEVA7.xml");

        // XML fájl DOM document való formába való alakítása
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xml);

        // a DOM document lévõ adatok módosítása
        DomModifier.modifyDom(document);

        // DOM document átalakítása DOM DocumentTraversal formába
        DocumentTraversal traversal = (DocumentTraversal) document;

        // DOM TreeWalker inicializálása
        // a gyökérelemtõl kezdve bejárhatjuk az összes elemet és szöveget tartalmazó
        // csomópontot
        TreeWalker walker = traversal.createTreeWalker(document.getDocumentElement(),
                NodeFilter.SHOW_ELEMENT | NodeFilter.SHOW_TEXT, null, true);

        // a DOM bejárása rekurzívan
        DomTraverser.traverseLevel(walker, "");
    }

    private static class DomModifier {
        public static void modifyDom(Document document) throws XPathExpressionException, DOMException, ParseException {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();

            // 1.) A Fehérgyarmati VSE edzõt cserélt, így az új edzõjük Varga Gergõ
            // XPath segítségével lekérdezzük a megfelelõ elemet/csomópontot a DOM
            // documentból
            Node csapat = (Node) xpath.evaluate("//csapat[./@csapatnev='Fehérgyarmati VSE']/@edzo",
                    document, XPathConstants.NODE);

            csapat.setTextContent("Varga Gergõ");

            // 2.)  // 2.) A bajnokságból a 4. helyezett csapatot kizárták, így amelyik csapat a 4. hely után végzett,
            //egyel elõrébb került a tabellán.
            NodeList csapatok = (NodeList) xpath.evaluate("//csapat[./hely_bajnoksag>4]/hely_bajnoksag", document,
                    XPathConstants.NODESET);

            for (int i = 0; i < csapatok.getLength(); i++) {
                csapat = csapatok.item(i);

                int helyezes = Integer.parseInt(csapat.getTextContent());
                csapat.setTextContent(Integer.toString(helyezes-1));
            }

            // 3.) Kovács Róbert az elõzõ meccsen szerzett 3 gólt, amit még nem adminisztráltak be
            Node jatekos = (Node) xpath.evaluate("//jatekos[./nev='Kovács Róbert']/golok", document,
                    XPathConstants.NODE);

            int golok=Integer.parseInt(jatekos.getTextContent());
            golok+=3;
            jatekos.setTextContent(Integer.toString(golok));

            // 4.) A 42-es járat át lett nevezve, mivel ez egy ID ezért az ehhez kapcsolódó
            // elemekben is módosítanunk kell a kapcsolatot
            // A Füzesabonyi SC vezetõsége átnevezte a klubot Sport Club helyett Kézilabda Clubra,
            //így az új nevük Füzesabonyi KC. Mivel attribútum több helyen is, így ott is módosítani kell
            NodeList nodes = (NodeList) xpath.evaluate("//csapat[@csapatnev='Füzesabonyi SC']/@csapatnev | //*[@csapat='Füzesabonyi SC']/@csapat "
            		+ "//*[@hazai='Füzesabonyi SC']/@hazai" +"//*[@vendeg='Füzesabonyi SC']/@vendeg | //*[@csapatnev='Füzesabonyi SC']/@csapatnev", document,
                    XPathConstants.NODESET);
            String ujNev = "Füzesabonyi KC";

            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);

                node.setTextContent(ujNev);
            }
        }
    }

    private static class DomTraverser {
        public static void traverseLevel(TreeWalker walker, String indent) {
            // kimentjük az aktuális csomópontot
            Node node = walker.getCurrentNode();

            // kiíratjuk a megfelelõ metódussal
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                printElementNode(node, indent);
            } else {
                printTextNode(node, indent);
            }

            // rekurzívan meghívjuk a bejárást a DOM fa eggyel mélyebben lévõ csomópontjára,
            // majd azok testvér csomópontjaira
            for (Node n = walker.firstChild(); n != null; n = walker.nextSibling()) {
                traverseLevel(walker, indent + "    ");
            }

            walker.setCurrentNode(node);
        }

        private static void printElementNode(Node node, String indent) {
            System.out.print(indent + node.getNodeName());

            printElementAttributes(node.getAttributes());
        }

        private static void printElementAttributes(NamedNodeMap attributes) {
            int length = attributes.getLength();

            if (length > 0) {
                System.out.print(" [ ");

                for (int i = 0; i < length; i++) {
                    Node attribute = attributes.item(i);

                    System.out.printf("%s=%s%s", attribute.getNodeName(), attribute.getNodeValue(),
                            i != length - 1 ? ", " : "");
                }

                System.out.println(" ]");
            } else {
                System.out.println();
            }
        }

        private static void printTextNode(Node node, String indent) {
            String content_trimmed = node.getTextContent().trim();

            if (content_trimmed.length() > 0) {
                System.out.print(indent);
                System.out.printf("{ %s }%n", content_trimmed);
            }
        }
    }
}
