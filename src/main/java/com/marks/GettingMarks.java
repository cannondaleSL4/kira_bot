package com.marks;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import java.io.*;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLOutput;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletionException;

@Slf4j
public class GettingMarks {

    private static String URL = "https://edu.gounn.ru";
    private static String USER_NAME = "Kostina Kira &mi";
    private static String RESULT = "./result";
    private static FileWriter fileWriter;
    private static FileReader fileReader;
    @NonNull
    private String username;
    @NonNull
    private String password;
    @NonNull
    private List<String> coockies;
    @NonNull
    private  CookieManager cookieManager;


    public GettingMarks(String username, String password) {
        this.username = username;
        this.password = password;
//        initCoockies();
    }

    @SneakyThrows
    public void initCoockies(){

        HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

        UncheckedObjectMapper objectMapper = new UncheckedObjectMapper();

        Map<String, String> data = new HashMap<>();
        data.put("username", username);
        data.put("password", password);

        JSONObject json = new JSONObject(data);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .uri(URI.create(URL + "/ajaxauthorize"))
                .setHeader("Content-Type", "application/json")
                .setHeader("origin", URL)
                .setHeader("referer", URL + "/authorize")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        this.coockies = response.headers().map().get("set-cookie");

        cookieManager = new CookieManager();
        cookieManager.put(URI.create(URL + "/ajaxauthorize"), response.headers().map());
    }

    @SneakyThrows
    public Map<String, List<String>> makeMarksRequest() {

        initCoockies();

        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .cookieHandler(cookieManager)
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();


        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(URL + "/journal-student-grades-action/u.1704?mode=excel"))
                .setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .setHeader("referer", URL + "/authorize")
                .setHeader("authority", "edu.gounn.ru")
                .build();
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        Map<String,List<String>> mapFromRequest = parseXlsAnswer(response.body());
        ObjectMapper objectMapper = new ObjectMapper();

        if (!Files.exists(Path.of(RESULT))) {
            saveInfFile(objectMapper.writeValueAsString(mapFromRequest));
            return mapFromRequest;
        }

        String fromFile = Files.readString(Path.of(RESULT));
        Map<String, List<String>> mapFromFile = objectMapper.readValue(fromFile, HashMap.class);
        MapDifference<String, List> diff = Maps.difference(mapFromRequest, mapFromFile);
        Map<String, List<String>> diffMap = new HashMap<>();

        diff.entriesDiffering().entrySet().stream().forEach( e -> {
            ArrayList<String> listFromRequest = (ArrayList<String>) e.getValue().leftValue();
            ArrayList<String> listFromFile = (ArrayList<String>) e.getValue().rightValue();
            List<String> list = new ArrayList<>(CollectionUtils.disjunction(listFromRequest, listFromFile));
            diffMap.put(e.getKey(), list);
                });

        if(diffMap.size() != 0){
            saveInfFile(objectMapper.writeValueAsString(mapFromRequest));
            return diffMap;
        }
        return new HashMap<>();
    }

    @SneakyThrows
    private void saveInfFile(String json) {
        Files.writeString(Path.of(RESULT), json);
    }

    @SneakyThrows
    private Map<String,List<String>> parseXlsAnswer(byte[] data ) {
        InputStream targetStream = new ByteArrayInputStream(data);
        XSSFWorkbook wb = new XSSFWorkbook(targetStream);
        XSSFSheet sheet = wb.getSheet(USER_NAME);
        XSSFRow row;
        XSSFCell cell;

        int rows; // No of rows
        rows = sheet.getPhysicalNumberOfRows();

        int cols = 0; // No of columns
        int tmp = 0;

        for(int i = 0; i < 10 || i < rows; i++) {
            row = sheet.getRow(i);
            if(row != null) {
                tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                if(tmp > cols) cols = tmp;
            }
        }
        Map<String,List<String>> mapLst = new HashMap<>();
        for(int r = 2; r < rows; r++) {
            List<String> marks  = new ArrayList<>();
            String schoolLesson = "";
            row = sheet.getRow(r);
            if(row != null) {
                for(int c = 0; c < cols; c++) {
                    cell = row.getCell((short)c);
                    if (c == 0) {
                        schoolLesson = cell.getStringCellValue();
                        mapLst.put(schoolLesson , new ArrayList<>());
                        continue;
                    }
                    if(cell != null && cell.getRawValue() != null ) {
                        if (cell.getCellType() == CellType.NUMERIC && !String.valueOf(cell.getNumericCellValue()).equals(" ")) {
                            double doubleNumber = cell.getNumericCellValue();
                            int intNumber = (int) doubleNumber;
                            if (intNumber > 10) {
                                String stringNumber = String.valueOf(intNumber);
                                marks.add(String.valueOf(stringNumber.charAt(0)));
                                marks.add(String.valueOf(stringNumber.charAt(1)));
                            } else {
                                marks.add(String.valueOf(intNumber));
                            }
                        } else if (!isCellEmpty(cell)) {
                            if (cell.getStringCellValue().contains("/") ) {
                                List<String> temp = new ArrayList<>(List.of(cell.getStringCellValue().split("/")));
                                temp.stream().forEach(element -> marks.add(element));
                            } else if (cell.getStringCellValue().contains("\\\\")) {
                                List<String> temp = new ArrayList<>(List.of(cell.getStringCellValue().split("\\\\")));
                                temp.stream().forEach(element -> marks.add(element));
                            } else {
                                try {
                                    Number number = NumberFormat.getInstance().parse(cell.getStringCellValue());
                                    marks.add(String.valueOf(number));
                                } catch (ParseException ex) {
                                    System.out.println(ex);
                                }
                            }
                        }
                    }
                }
                mapLst.put(schoolLesson, marks);
            }
        }
        return mapLst;
    }

    public static boolean isCellEmpty(final XSSFCell cell) {
        if (cell == null) { // use row.getCell(x, Row.CREATE_NULL_AS_BLANK) to avoid null cells
            return true;
        }

        if (cell.getCellType() == CellType.BLANK) {
            return true;
        }

        if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().replaceAll("\u00A0", "").isEmpty()) {
            return true;
        }

        return false;
    }

    class UncheckedObjectMapper extends com.fasterxml.jackson.databind.ObjectMapper {
        Map<String, String> readValue(String content) {
            try {
                return this.readValue(content, new TypeReference<>() {
                });
            } catch (IOException ioe) {
                throw new CompletionException(ioe);
            }
        }
    }
}
