package com.nixsolutions.logging.advice;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.junit.jupiter.api.Assertions;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class LoggingResultHelper
{
  public static final String PARAM_STR = "STR_PARAM";
  public static final Long PARAM_LONG = 1L;

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public static class JsonLogAssertion
  {
    private ByteArrayOutputStream baos;
    private Queue<String> nestedProperties = new LinkedList<>();
    private List<String> propertyValuesIgnored = new ArrayList<>();

    public JsonLogAssertion givenSource(ByteArrayOutputStream byteArrayOutputStream)
    {
      this.baos = byteArrayOutputStream;
      return this;
    }

    public JsonLogAssertion fromCtx()
    {
      return fromProperty("context").fromProperty("ctx");
    }

    public JsonLogAssertion fromProperty(String propertyName)
    {
      nestedProperties.offer(propertyName);
      return this;
    }

    public JsonLogAssertion propertyValueIgnored(String property)
    {
      propertyValuesIgnored.add(property);
      return this;
    }

    public void shouldBeEqualTo(JsonNode jsonNode)
    {
      try
      {
        JsonNode nodeToTraverse = OBJECT_MAPPER.readTree(baos.toString());

        for (String property : nestedProperties)
        {
          nodeToTraverse = nodeToTraverse.get(property);
        }

        for (String property : propertyValuesIgnored)
        {
          nullifyField(jsonNode, property);
          nullifyField(nodeToTraverse, property);
        }

        Assertions.assertEquals(jsonNode, nodeToTraverse);

      }
      catch (Exception ex)
      {
        throw new RuntimeException(ex);
      }
    }

    private void nullifyField(JsonNode node, String property)
    {
      String[] props = property.split("\\.");

      ObjectNode objectNode = new ObjectNode(null, null);

      for (int i = 0; i < props.length - 1; i++)
      {
        objectNode = (ObjectNode) node.get(props[i]);
      }

      objectNode.put(props[props.length - 1], "");

    }
  }

  public static JsonLogAssertion supposeThat()
  {
    return new JsonLogAssertion();
  }
}
