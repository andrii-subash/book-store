package book.store.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record BookSearchParametersDto(List<String> title, List<String> author) {
    public Map<String, List<String>> getParams() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("title", title());
        params.put("author", author());
        return params;
    }
}
