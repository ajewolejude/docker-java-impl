package ai.openfabric.api.util;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class UtilsService {

    public Map<String, Object> paginationDetails(Map<String, Object> map, Page<?> page) {
        int totalPages = page.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            map.put("page_numbers", pageNumbers);
        } else {
            map.put("page_numbers", new ArrayList<>());
        }

        map.put("number", page.getNumber());
        map.put("total_pages", totalPages);

        return map;
    }

}
