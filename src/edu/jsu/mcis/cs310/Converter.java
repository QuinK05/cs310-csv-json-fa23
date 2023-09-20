package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> full = reader.readAll();

            Iterator<String[]> iterator = full.iterator();

            JsonObject json = new JsonObject();

            JsonArray prodNums = new JsonArray();
            JsonArray data = new JsonArray();

            if (iterator.hasNext()) {
                String[] headings = iterator.next();

                while (iterator.hasNext()) {
                    String[] row = iterator.next();

                    JsonArray dataRow = new JsonArray();

                    prodNums.add(row[0]);

                    for (int i = 1; i < row.length; i++) {
                        try {
                            int intValue = Integer.parseInt(row[i]);
                            dataRow.add(intValue);
                        } catch (NumberFormatException e) {
                            dataRow.add(row[i]);
                        }
                    }
                    data.add(dataRow);
                }
                json.put("ProdNums", prodNums);
                json.put("ColHeadings", headings);
                json.put("Data", data);
            }

            result = Jsoner.serialize(json);
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try { 
           JsonObject json = Jsoner.deserialize(jsonString, new JsonObject());
           StringWriter writer = new StringWriter();
           CSVWriter csvWriter = new CSVWriter(writer, ',', '"', '\\', "\n");
           
           JsonArray colHeadings = (JsonArray) json.get("ColHeadings");
           String[] headings = colHeadings.toArray(String[]::new);
                   
                csvWriter.writeNext(headings);

            JsonArray prodNums = (JsonArray) json.get("ProdNums");
            JsonArray dataJson = (JsonArray) json.get("Data");
            
            
            for (int i = 0; i < dataJson.size(); i++){
                JsonArray row = (JsonArray) dataJson.get(i);
                String[] data = new String[row.size() + 1];
                
                data[0] = prodNums.getString(i);
                
                for (int j = 0; j < row.size(); j++){
                    String value = row.getString(j);
                    int column = j + 1;
                    
                    if(column == 3){
                        
                        try {
                            int intValue = Integer.parseInt(value);
                            data[column] = String.format("%02d", intValue);
                        }
                         catch (NumberFormatException e){
                             
                        }
                    }
                    
                    else {
                        data[column] = value;
                    }
                }
                
                csvWriter.writeNext(data);  

            }
            
             String csv = writer.toString();

            result = csv;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}