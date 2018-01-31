package com.genericgraph.api;

import java.util.ArrayList;
import org.neo4j.graphdb.*;

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

    public boolean writeRelationship(GenericNode firstNode, Relationship relationship, GenericNode secondNode) {
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

    public Node find(GenericNode node) {
        String queryStart = "MATCH (n) WHERE ";
        String queryEnd = "RETURN n";
        
        String values = getValuesString(node);
        String labels = getLabelString(node);

        String query = queryStart + values + labels + queryEnd;
        return (Node)db.execute(query).next().get("n");
    }

    private String getValuesString(GenericNode node) {
        StringBuilder values = new StringBuilder();
        node.values.forEach((k,v) -> {
            values.append("n." + k + " = '" + v + "' AND ");
        });

        String query = "";
        if (values.length() > 4) {
            query = values.toString().substring(0, values.length()-4);
        }
        return query;
    }

    private String getLabelString(GenericNode node) {
        if (node.label.size() > 0) {
            StringBuilder labels = new StringBuilder();
            node.label.forEach(s -> {
                labels.append(":" + s);
            });
            return "AND n" + labels.toString() + " ";
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
}