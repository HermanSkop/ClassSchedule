package parser;

import table.Cell;
import table.Table;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public
class ScheduleHandler {
    public static class Pair {
        int a;
        int b;
        Pair(int a, int b){
            this.a = a;
            this.b = b;
        }

        public int getFirst() {
            return a;
        }

        public int getSecond() {
            return b;
        }

        @Override
        public boolean equals(Object pair) {
            if(!(pair instanceof Pair)) return false;
            else return ((Pair) pair).a==this.a && ((Pair) pair).b == this.b;
        }

        @Override
        public String toString() {
            return "(" + a + ", " + b + ")";
        }
    }

    private static String getFormDataAsString(Map<String, String> formData) {
        StringBuilder formBodyBuilder = new StringBuilder();
        for (Map.Entry<String, String> singleEntry : formData.entrySet()) {
            if (formBodyBuilder.length() > 0)
                formBodyBuilder.append("&");
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getKey(), StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(singleEntry.getValue(), StandardCharsets.UTF_8));
        }
        return formBodyBuilder.toString();
    }

    public static String getPage(String date) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        Map<String, String> formData = new HashMap<>();
        formData.put("DataPicker$dateInput", date);
        formData.put("DataPicker_dateInput_ClientState", "{\"enabled\":true,\"emptyMessage\":\"\",\"validationText\":\""+date+"-00-00-00\",\"valueAsString\":\""+date+"-00-00-00\",\"minDateStr\":\"1980-01-01-00-00-00\",\"maxDateStr\":\"2099-12-31-00-00-00\",\"lastSetTextBoxValue\":\""+date+"\"}");
        formData.put("__EVENTTARGET", "DataPicker");

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(getFormDataAsString(formData)))
                .uri(URI.create("https://planzajec.pjwstk.edu.pl/PlanOgolny3.aspx"))
                .setHeader("User-Agent", "Mozilla/5.0") // add request header
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("referer", "https://planzajec.pjwstk.edu.pl/PlanOgolny3.aspx")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public static Element getTable(String page) {
        Document document = Jsoup.parse(page);
        return document.getElementById("ZajeciaTable");
    }

    public static Element getBody(Element element) {
        return element.getElementsByTag("tbody").get(0);
    }

    public static Table parseTable(Element htmlTable, String filter){
        List<String> descClasses = new ArrayList<>();
        descClasses.add("pelnaGodzina title");
        descClasses.add("minuty title");
        descClasses.add("dzien title");

        Table table = new Table();
        List<Pair> merged = new LinkedList<>();
        int rowId = 0;
        for (Element htmlRow:htmlTable.getElementsByTag("tr")) {
            int colId = 0;
            int expandColId = 0;
            for (Element htmlCell:htmlRow.getElementsByTag("td")) {
                while(merged(rowId, colId+expandColId, merged)){
                    expandColId++;
                }

                String content = parseLabel(htmlCell.ownText());
                int span = 0;

                Cell cell = new Cell();
                cell.setRowId(rowId);
                cell.setColId(colId+expandColId);
                if(descClasses.contains(htmlCell.className())) cell.setDescription(true);

                if(cell.isDescription() || content.toLowerCase().contains(filter.toLowerCase())) {
                    cell.setStyle(Arrays.stream(htmlCell.attr("style").split(";")).map(element -> Objects.equals(element, "") ?"":"-fx-" + element).collect(Collectors.joining(";")));
                    cell.setContent(content);
                    if (!htmlCell.attr("colspan").equals("")) {
                        span = Integer.parseInt(htmlCell.attr("colspan"));
                        mergeCols(rowId, colId + expandColId, span, merged);
                    }
                    if (!htmlCell.attr("rowspan").equals("")) {
                        span = Integer.parseInt(htmlCell.attr("rowspan"));
                        mergeRows(rowId, colId + expandColId, span, merged);
                    }
                }
                cell.setSpan(span);
                table.add(cell);
                colId++;
            }
            rowId++;
        }
        return table;
    }
    private static String parseLabel(String line){
        if(line.split(" ").length>3) return Arrays.stream(line.split("[\\s-]+")).map(e->e + "\n").collect(Collectors.joining(""));
        return line;
    }

    private static boolean merged(int row, int col, List<Pair> merged){
        return merged.contains(new Pair(row, col));
    }

    private static void mergeCols(int row, int col, int span, List<Pair> merged){
        for (int i = 0; i<span; i++){
            merged.add(new Pair(row, col + i));
        }
    }

    private static void mergeRows(int row, int col, int span, List<Pair> merged){
        for (int i = 0; i<span; i++){
            merged.add(new Pair(row + i, col));
        }
    }


}

