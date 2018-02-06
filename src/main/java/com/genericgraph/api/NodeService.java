package com.genericgraph.api;

import java.util.ArrayList;
import org.neo4j.graphdb.*;
import java.util.HashMap;
import com.genericgraph.api.domain.*;

class NodeService {
    protected GraphDatabaseService db;

    NodeService(GraphDatabaseService injectedDb) {
        this.db = injectedDb;
    }

    public boolean write(GenericNode gNode) {
        Transaction tx = db.beginTx();
        Node node = db.createNode();

        addLabels(node, gNode);
        addValues(node, gNode);

        tx.success();
        return true;
    }

    private void addLabels(Node node, GenericNode gNode) {
        if (gNode.label != null) {
            gNode.label.forEach(it -> {
                node.addLabel(Label.label(it));
            });
        }
    }

    private void addValues(Node node, GenericNode gNode) {
        if (gNode.values != null) {
            gNode.values.forEach((k,v) ->{
                node.setProperty(k, v);
            });
        }
    }

    public boolean writeRelationship(GenericNode firstNode, GenericRelationship relationship, GenericNode secondNode) {
        Transaction tx = db.beginTx();

        Node firstFoundNode = find(firstNode);
        Node secondFoundNode = find(secondNode);

        org.neo4j.graphdb.Relationship graphRelationship = firstFoundNode.createRelationshipTo(secondFoundNode, RelationshipType.withName(relationship.name));

        if(relationship.values != null) {
            relationship.values.forEach( (String k, Object v) -> {
                graphRelationship.setProperty(k, v);
            });
        }

        tx.success();
        return true;
    }

    //TODO:De-dup all this garbage
    public Node find(GenericNode node) {
        String queryStart = "MATCH (n) WHERE ";
        String queryEnd = "RETURN n";
        
        String values = getValuesString(node.values, "=");
        String labels = getLabelString(node);
        String where = values != "" && labels != "" ? values + "AND " + labels : values + labels;

        String query = queryStart + where + queryEnd;
        
        return (Node)db.execute(query).next().get("n");
    }

    public Node find(GenericQuery query) {
        String queryStart = "MATCH (n) WHERE ";
        String queryEnd = "RETURN n";

        String values = getValuesString(query.nodes);

        String where = values;
        String queryString = queryStart + where + queryEnd;

        System.out.println(queryString);

        return (Node)db.execute(queryString).next().get("n");
    }

    private String getValuesString(HashMap<String,Object> values, String operator) {
        StringBuilder valuesString = new StringBuilder();
        values.forEach((k,v) -> {
            String value = v instanceof Integer ? v.toString() : "'" + v + "'";
            valuesString.append("n." + k + " "+ operator +" " + value + " AND ");
        });

        String query = "";
        if (valuesString.length() > 4) {
            query = valuesString.toString().substring(0, valuesString.length()-4);
        }
        return query;
    }

    private String getValuesString(ArrayList<QueryParameter> parameters) {
        StringBuilder valuesString = new StringBuilder();

        parameters.forEach((QueryParameter p) -> {
            valuesString.append(p.toString() + " AND ");
        });

        String query = "";
        if (valuesString.length() > 4) {
            query = valuesString.toString().substring(0, valuesString.length()-4);
        }
        return query;
    }

    private String getLabelString(GenericNode node) {
        if (node.label.size() > 0) {
            StringBuilder labels = new StringBuilder();
            node.label.forEach(s -> {
                labels.append(":" + s);
            });
            return "n" + labels.toString() + " ";
        }
        return "";
    }

    public ArrayList<org.neo4j.graphdb.Relationship> findRelationships(String name) {
        ArrayList<org.neo4j.graphdb.Relationship> relationshipResults = new ArrayList<>();

        Result result = db.execute("MATCH ()-[n:"+ name + "]-() RETURN n");
        ResourceIterator<org.neo4j.graphdb.Relationship> relationships = result.columnAs("n");
        relationships.forEachRemaining(it -> {
            relationshipResults.add(it);
        });

        return relationshipResults;
    }

    public ArrayList<org.neo4j.graphdb.Relationship> findRelationships(String name, String operator, HashMap<String,Object> values) {
        ArrayList<org.neo4j.graphdb.Relationship> relationshipResults = new ArrayList<>();

        String whereString = "";
        if(values != null && values.size() > 0) {
            whereString = "WHERE " + getValuesString(values, operator);
        }

        Result result = db.execute("MATCH ()-[n:"+ name + "]-() " + whereString + " RETURN n");
        
        ResourceIterator<org.neo4j.graphdb.Relationship> relationships = result.columnAs("n");
        relationships.forEachRemaining(it -> {
            relationshipResults.add(it);
        });

        return relationshipResults;
    }
}