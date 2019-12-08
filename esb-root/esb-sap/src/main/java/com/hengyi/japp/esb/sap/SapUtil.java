package com.hengyi.japp.esb.sap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.github.ixtf.japp.core.J;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sap.conn.jco.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.github.ixtf.japp.core.Constant.MAPPER;

/**
 * 描述：
 *
 * @author jzb 2018-03-18
 */
public class SapUtil {
    /**
     * @param f
     * @param body json 文本
     */
    public static final void setParam(final JCoFunction f, final String body) throws Exception {
        if (J.isBlank(body)) {
            return;
        }
        final JsonNode node = MAPPER.readTree(body);
        setParam(f.getImportParameterList(), node.get("imports"));
        setParam(f.getChangingParameterList(), node.get("changings"));
        setParam(f.getTableParameterList(), node.get("tables"));
    }

    private static final void setParam(final JCoRecord record, final JsonNode node) {
        if (record == null || node == null) {
            return;
        }
        StreamSupport.stream(record.spliterator(), true).forEach(field -> {
            final String fieldName = field.getName();
            final JsonNode fieldNode = node.get(fieldName);
            Optional.ofNullable(getValue(fieldNode, field)).ifPresent(field::setValue);
        });
    }

    private static final Object getValue(final JsonNode fieldNode, final JCoField field) {
        if (fieldNode == null || fieldNode.isNull()) {
            return null;
        }
        switch (field.getType()) {
            case JCoMetaData.TYPE_STRUCTURE: {
                JCoStructure structure = field.getStructure();
                setParam(structure, fieldNode);
                return structure;
            }
            case JCoMetaData.TYPE_TABLE: {
                JCoTable table = field.getTable();
                if (!fieldNode.isArray()) {
                    throw new RuntimeException("JcoField[" + field + "]，为 JCoTable，但 fieldNode 不是 ArrayNode");
                }
                ArrayNode arrayNode = (ArrayNode) fieldNode;
                arrayNode.forEach(rowNode -> {
                    table.appendRow();
                    JCoRecord record = table;
                    setParam(record, rowNode);
                });
                return table;
            }
            case JCoMetaData.TYPE_DATE: {
                if (fieldNode.isNumber()) {
                    final long l = fieldNode.asLong();
                    return new Date(l);
                }
                return fieldNode.asText();
            }
            default: {
                return fieldNode.asText();
            }
        }
    }

    public static final String params2String(JCoFunction f) throws Exception {
        final Map<String, Object> map = Maps.newHashMap();
        map.put("imports", toMap(f.getImportParameterList()));
        map.put("exports", toMap(f.getExportParameterList()));
        map.put("changings", toMap(f.getChangingParameterList()));
        map.put("tables", toMap(f.getTableParameterList()));
        return MAPPER.writeValueAsString(map);
    }

    public static final Map<String, Object> toMap(final JCoRecord record) {
        if (record == null) {
            return Collections.EMPTY_MAP;
        }
        return StreamSupport.stream(record.spliterator(), true)
                .collect(Collectors.toMap(JCoField::getName, SapUtil::getValue));
    }

    private static final Object getValue(final JCoField field) {
        switch (field.getType()) {
            case JCoMetaData.TYPE_STRUCTURE: {
                JCoStructure structure = field.getStructure();
                return toMap(structure);
            }
            case JCoMetaData.TYPE_TABLE: {
                JCoTable table = field.getTable();
                int numRows = table.getNumRows();
                if (numRows < 1) {
                    return Collections.EMPTY_LIST;
                }
                Collection collection = Lists.newArrayList();
                do {
                    JCoRecord record = table;
                    Map<String, Object> map = toMap(record);
                    collection.add(map);
                } while (table.nextRow());
                return collection;
            }
            default: {
                return Optional.ofNullable(field.getValue()).orElse(NullNode.instance);
            }
        }
    }

}
