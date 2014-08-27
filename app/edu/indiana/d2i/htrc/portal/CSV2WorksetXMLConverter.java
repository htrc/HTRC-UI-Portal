package edu.indiana.d2i.htrc.portal;

import au.com.bytecode.opencsv.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import play.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * Created by shliyana on 7/2/14.
 */
public class CSV2WorksetXMLConverter {
    private static Logger.ALogger log = play.Logger.of("application");

    public static Document convert(File csv) throws Exception {
        int rowsCount = -1;

        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
            Document volumesDoc = domBuilder.newDocument();

            Element volumesEle = volumesDoc.createElement("volumes");
            volumesDoc.appendChild(volumesEle);

            au.com.bytecode.opencsv.CSVReader csvReader = new au.com.bytecode.opencsv.CSVReader(new FileReader(csv));

            List<String[]> rows = csvReader.readAll();
            if (rows.size() <= 0) {
                log.warn("Empty Workset CSV.");
                throw new Exception("Empty Workset CSV.");
            }

            String[] headers = rows.remove(0);
            if (headers.length <= 0) {
                log.warn("Empty headers list.");
                throw new Exception("Empty headers list.");
            }

            for (String[] row : rows) {
                Element volumeEle = volumesDoc.createElement("volume");
                volumesEle.appendChild(volumeEle);

                Element propsEle = volumesDoc.createElement("properties");

                for (int i = 0; i < row.length; i++) {
                    if (i == 0) {
                        Element idEle = volumesDoc.createElement("id");
                        idEle.setTextContent(row[i]);
                        volumeEle.appendChild(idEle);
                    } else {
                        Element propertyEle = volumesDoc.createElement("property");
                        propertyEle.setAttribute("name", headers[i]);
                        propertyEle.setAttribute("value", row[i]);
                        propsEle.appendChild(propertyEle);
                    }
                }

                if (row.length > 1) {
                    volumeEle.appendChild(propsEle);
                }
            }

            return volumesDoc;
        } catch (Exception exp) {
            log.error("Error converting CSV to XML.", exp);
            throw exp;
        }
    }
}
