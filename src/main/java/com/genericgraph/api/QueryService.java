package com.genericgraph.api;

import com.genericgraph.api.domain.*;

class QueryService {

    NodeService nodeService;

    QueryService(NodeService nodeService) {
        this.nodeService = nodeService;
    }
    
    void find(GenericQuery query) {
        // GenericNode queryNode = new GenericNode();
        // query.nodes.forEach(param -> {
        //     queryNode.values.put(key, value);
        // });
        // nodeService.find();
    }
}