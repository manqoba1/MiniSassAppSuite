package com.sifiso.codetribe.minisasslibrary.services;

import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverTownDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;

import java.util.List;

/**
 * Created by sifiso on 4/16/2015.
 */
public interface CreateEvaluationListener {
    public void onCreateEvaluation(ResponseDTO response);

    public void onRefreshEvaluation(List<EvaluationSiteDTO> siteList, int index);

    public void onRefreshTown(List<RiverTownDTO> riverTownList, int index);

    public void onRefreshMap(RiverDTO river, int result);

    public void onCreateEvaluationRL(RiverDTO river);
}
