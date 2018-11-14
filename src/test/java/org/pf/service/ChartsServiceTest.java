package org.pf.service;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ChartsServiceTest {

    @Test
    public void removeZeroColumns() {

//        public void removeZeroColumns(List<Map<String, Object>> data) {

        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("Month", "Jan");
        row1.put("acc1", 1.1);
        row1.put("zero", 0.0);
        row1.put("Total", 1.1);

        data.add((HashMap<String, Object>) row1);

        Map<String, Object> row2 = new HashMap<>();
        row2.put("Month", "Feb");
        row2.put("acc1", 2.0);
        row2.put("zero", 0.0);
        row2.put("Total", 2.0);

        data.add((HashMap<String, Object>) row2);

        ChartsService charts = new ChartsService(null, null, null, null);

        charts.removeZeroColumns(data);
        assertEquals(2, data.size());
        assertNull(data.get(0).get("zero"));
        assertNotNull(data.get(0).get("acc1"));

    }

}
