package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import org.apache.commons.collections4.CollectionUtils;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;


public class Main {
    public static String CONFIGFILENAME = "config.dat";

    public static void main(String[] args) throws IOException {

        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.print("Hello and welcome!\n");

        Config config = setup();
        File answerJsonFile = new File(config.getAnswerName());

        LocalDate beginningDate = config.getBeginningDate();
        LocalDate endingDate = config.getEndingDaten();

        List<Week> listOfWeeks = new ArrayList<>();

        var calcdate = beginningDate;
        while (calcdate.isBefore(endingDate)) {
            listOfWeeks.add(new Week(calcdate));
            calcdate = calcdate.plusWeeks(1);
        }
        Map<String, String> valueMap;


        List<String> randomValueMap = fillListWithStringsOrCreateNewAndFillWithExample(config);

        Map<String, String> varMap = fillMapWithStringsOrCreateNewAndFillWithExample(new File(config.getVarFileName()), config);
        //final Map<String,String> finMap = valueMap;
        //final Map<String, String> variableMap = varMap;

        try {

            for (Week week : listOfWeeks) {
                PdfReader reader = new PdfReader(config.getFileName());
                valueMap = fillMapWithAnswerFileOrCreateNew(answerJsonFile, reader, config);
                //PdfReader newReader = new PdfReader(config.getFileName());
                PdfStamper stamper = new PdfStamper(reader,
                        new FileOutputStream(week.getStartDate().toString()
                                + "-" + week.getEndDate() + ".pdf"));
                var acroFields = stamper.getAcroFields();
                Map<String, String> replacedMap = new HashMap<>();
                valueMap.entrySet().stream().forEach(entry -> {
                            var value = entry.getValue().replace("%startDate", week.getStartDate().toString());
                            value = value.replace("%endDate", week.getEndDate().toString());
                            value = value.replace("%fridayDate", week.getFriday().toString());
                            if (value.contains("%randomValue"))
                                value = value.replace("%randomValue", fillWithRandomValues(5, randomValueMap));
                            for (Map.Entry<String, String> varEntry : varMap.entrySet()) {
                                value = value.replace(varEntry.getKey(), varEntry.getValue());
                            }
                            replacedMap.put(entry.getKey(), value);
                        }
                );
                valueMap = replacedMap;
                for (Map.Entry<String, String> entry : valueMap.entrySet()) {
                    try {
                        acroFields.setField(entry.getKey(), entry.getValue());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (DocumentException e) {
                        throw new RuntimeException(e);
                    }
                }
                reader.close();
                stamper.close();
            }
        } catch (IOException e) {
            System.out.println("Error ioex");
        } catch (DocumentException e) {
            System.out.println("Error documentex");
        }

    }

    private static String stringReplacer(String mapValue, String variable, String variableValue) {
        return mapValue.replace(variable, variableValue);
    }

    private static Map<String, String> fillMapWithAnswerFileOrCreateNew(File answerJsonFile, PdfReader reader, Config config) throws IOException {
        try {
            return read(answerJsonFile);
        } catch (IOException critE) {
            System.out.print("Creating new answer file, old is  missing");
            export(reader.getAcroFields(), config);
            return read(answerJsonFile);
        }
    }

    private static String fillWithRandomValues(int valueCount, List<String> randomValueList) {
        String returnedString = "";
        if (valueCount < 0 || randomValueList.isEmpty()) return returnedString;

        for (int i = 0; i < valueCount; i++) {
            if (i == 0) {
                String popString = randomValueList.stream().findFirst().orElseThrow();
                returnedString = returnedString + popString;
                randomValueList.remove(popString);
            } else {
                if (CollectionUtils.isNotEmpty(randomValueList)) {
                    String popString = randomValueList.stream().findFirst().orElseThrow();
                    randomValueList.remove(popString);
                    returnedString = returnedString + ", " + popString;
                } else break;
            }
        }

        return returnedString;
    }

    private static Map<String, String> fillMapWithStringsOrCreateNewAndFillWithExample(File file, Config config) throws IOException {
        try {
            return read(file);
        } catch (IOException critE) {
            System.out.print("Creating Variable Map With one Example");
            export(file);
            return read(file);
        }
    }

    private static List<String> fillListWithStringsOrCreateNewAndFillWithExample(Config config) throws IOException {
        try {
            return readJsonToList(new File(config.getRandomValueListFileName()), new TypeReference<List<String>>() {
            });
        } catch (IOException critE) {
            System.out.print("Creating Random List With 2 Examples");
            exportList(new File(config.getRandomValueListFileName()));
            return readJsonToList(new File(config.getRandomValueListFileName()), new TypeReference<List<String>>() {
            });
        }
    }

    private static Config setup() throws JsonProcessingException, IOException {
        var config = new Config();
        try {
            return readConfig(new File(CONFIGFILENAME));
        } catch (IOException e) {
            System.out.println("Error reading the File, creating a new one, hope you made a Backup :-)");
            export(config);
            return config;
        }
    }

    public static Map<String, String> read(File file) throws JsonProcessingException, IOException {
        Map<String, String> response;
        TypeReference<HashMap<String, String>> typeReference = new TypeReference<>() {
        };
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        response = mapper.readValue(file, typeReference);
        return response;
    }

    public static <T> List<T> readJsonToList(File file, TypeReference<List<T>> typeReference) throws IOException {
        return new ObjectMapper().readValue(file, typeReference);
    }

    public static Config readConfig(File file) throws JsonProcessingException, IOException {
        TypeReference<Config> typeReference = new TypeReference<>() {
        };
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        var response = mapper.readValue(file, typeReference);
        return response;
    }

    public static void export(AcroFields acroFields, Config config) throws JsonProcessingException, FileNotFoundException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        var fieldmap = acroFields.getFields();
        Map<String, String> entryMap = new HashMap<>();
        int counter = 1;
        for (Object entry : fieldmap.keySet()) {
            entryMap.put(entry.toString(), "Field" + counter++);
        }
        String jacksonData = objectMapper.writeValueAsString(entryMap);
        File file = new File(config.getAnswerName());
        try {
            var foStream = new FileOutputStream(file);
            foStream.write(jacksonData.getBytes(StandardCharsets.UTF_16));
            foStream.flush();
            foStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error exporting json");
        } catch (IOException e) {
            System.out.println("Error exporting json");
        }
    }

    public static void export(Config config) throws JsonProcessingException, FileNotFoundException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jacksonData = objectMapper.writeValueAsString(config);
        File file = new File(CONFIGFILENAME);
        var foStream = new FileOutputStream(file);
        foStream.write(jacksonData.getBytes(StandardCharsets.UTF_16));
        foStream.flush();
        foStream.close();
    }

    public static void export(File file) throws JsonProcessingException, FileNotFoundException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jacksonData = objectMapper.writeValueAsString(Map.of("%exampleValueToReplace", "this is the replacement text"));

        var foStream = new FileOutputStream(file);
        foStream.write(jacksonData.getBytes(StandardCharsets.UTF_16));
        foStream.flush();
        foStream.close();
    }

    public static void exportList(File file) throws JsonProcessingException, FileNotFoundException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jacksonData = objectMapper.writeValueAsString(List.of("Random Value 2", "Random Example Value 2"));

        var foStream = new FileOutputStream(file);
        foStream.write(jacksonData.getBytes(StandardCharsets.UTF_16));
        foStream.flush();
        foStream.close();
    }
}