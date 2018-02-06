package com.genericgraph.api;

import com.genericgraph.api.domain.*;

class QueryService {

    NodeService nodeService;

    QueryService(NodeService nodeService) {
        this.nodeService = nodeService;
    }
    
    GenericNode find(GenericQuery query) {
        return new GenericNode(nodeService.find(query));
    }
}