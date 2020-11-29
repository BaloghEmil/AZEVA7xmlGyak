
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
        // XML f�jl beolvas�sa
        File xml = new File("XMLAZEVA7.xml");

        // XML f�jl DOM document val� form�ba val� alak�t�sa
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xml);

        // a DOM document l�v� adatok m�dos�t�sa
        DomModifier.modifyDom(document);

        // DOM document �talak�t�sa DOM DocumentTraversal form�ba
        DocumentTraversal traversal = (DocumentTraversal) document;

        // DOM TreeWalker inicializ�l�sa
        // a gy�k�relemt�l kezdve bej�rhatjuk az �sszes elemet �s sz�veget tartalmaz�
        // csom�pontot
        TreeWalker walker = traversal.createTreeWalker(document.getDocumentElement(),
                NodeFilter.SHOW_ELEMENT | NodeFilter.SHOW_TEXT, null, true);

        // a DOM bej�r�sa rekurz�van
        DomTraverser.traverseLevel(walker, "");
    }

    private static class DomModifier {
        public static void modifyDom(Document document) throws XPathExpressionException, DOMException, ParseException {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();

            // 1.) A Feh�rgyarmati VSE edz�t cser�lt, �gy az �j edz�j�k Varga Gerg�
            // XPath seg�ts�g�vel lek�rdezz�k a megfelel� elemet/csom�pontot a DOM
            // documentb�l
            Node csapat = (Node) xpath.evaluate("//csapat[./@csapatnev='Feh�rgyarmati VSE']/@edzo",
                    document, XPathConstants.NODE);

            csapat.setTextContent("Varga Gerg�");

            // 2.)  // 2.) A bajnoks�gb�l a 4. helyezett csapatot kiz�rt�k, �gy amelyik csapat a 4. hely ut�n v�gzett,
            //egyel el�r�bb ker�lt a tabell�n.
            NodeList csapatok = (NodeList) xpath.evaluate("//csapat[./hely_bajnoksag>4]/hely_bajnoksag", document,
                    XPathConstants.NODESET);

            for (int i = 0; i < csapatok.getLength(); i++) {
                csapat = csapatok.item(i);

                int helyezes = Integer.parseInt(csapat.getTextContent());
                csapat.setTextContent(Integer.toString(helyezes-1));
            }

            // 3.) Kov�cs R�bert az el�z� meccsen szerzett 3 g�lt, amit m�g nem adminisztr�ltak be
            Node jatekos = (Node) xpath.evaluate("//jatekos[./nev='Kov�cs R�bert']/golok", document,
                    XPathConstants.NODE);

            int golok=Integer.parseInt(jatekos.getTextContent());
            golok+=3;
            jatekos.setTextContent(Integer.toString(golok));

            // 4.) A 42-es j�rat �t lett nevezve, mivel ez egy ID ez�rt az ehhez kapcsol�d�
            // elemekben is m�dos�tanunk kell a kapcsolatot
            // A F�zesabonyi SC vezet�s�ge �tnevezte a klubot Sport Club helyett K�zilabda Clubra,
            //�gy az �j nev�k F�zesabonyi KC. Mivel attrib�tum t�bb helyen is, �gy ott is m�dos�tani kell
            NodeList nodes = (NodeList) xpath.evaluate("//csapat[@csapatnev='F�zesabonyi SC']/@csapatnev | //*[@csapat='F�zesabonyi SC']/@csapat "
            		+ "//*[@hazai='F�zesabonyi SC']/@hazai" +"//*[@vendeg='F�zesabonyi SC']/@vendeg | //*[@csapatnev='F�zesabonyi SC']/@csapatnev", document,
                    XPathConstants.NODESET);
            String ujNev = "F�zesabonyi KC";

            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);

                node.setTextContent(ujNev);
            }
        }
    }

    private static class DomTraverser {
        public static void traverseLevel(TreeWalker walker, String indent) {
            // kimentj�k az aktu�lis csom�pontot
            Node node = walker.getCurrentNode();

            // ki�ratjuk a megfelel� met�dussal
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                printElementNode(node, indent);
            } else {
                printTextNode(node, indent);
            }

            // rekurz�van megh�vjuk a bej�r�st a DOM fa eggyel m�lyebben l�v� csom�pontj�ra,
            // majd azok testv�r csom�pontjaira
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
