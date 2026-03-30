package com.nanqiong.cppcpatient.service;

import com.nanqiong.cppcpatient.dto.CppcTagNodeResponse;
import com.nanqiong.cppcpatient.repository.CppcTagRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CppcTagService {

    private final CppcTagRepository cppcTagRepository;

    public CppcTagService(CppcTagRepository cppcTagRepository) {
        this.cppcTagRepository = cppcTagRepository;
    }

    public List<CppcTagNodeResponse> getTree() {
        List<CppcTagRepository.CppcTagEntity> tags = cppcTagRepository.findAll();
        Map<Long, CppcTagNodeResponse> nodeMap = new LinkedHashMap<>();
        List<CppcTagNodeResponse> roots = new ArrayList<>();

        for (CppcTagRepository.CppcTagEntity tag : tags) {
            nodeMap.put(tag.id(), new CppcTagNodeResponse(
                    tag.id(),
                    tag.parentId(),
                    tag.bizCode(),
                    tag.label(),
                    tag.level(),
                    tag.isLeaf()
            ));
        }

        for (CppcTagRepository.CppcTagEntity tag : tags) {
            CppcTagNodeResponse current = nodeMap.get(tag.id());
            if (tag.parentId() == 0) {
                roots.add(current);
                continue;
            }
            CppcTagNodeResponse parent = nodeMap.get(tag.parentId());
            if (parent != null) {
                parent.getChildren().add(current);
            }
        }

        return roots;
    }
}
