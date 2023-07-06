package com.hankcs.model;

import com.hankcs.cfg.HanlpPath;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.model.crf.CRFSegmenter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.elasticsearch.common.io.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Project: elasticsearch-analysis-hanlp
 * Description:
 * Author: Kenn
 * Create: 2021-01-30 01:22
 */
public class CRFSegmenterInstance {

    private static final Logger logger = LogManager.getLogger(CRFSegmenterInstance.class);

    private static volatile CRFSegmenterInstance instance = null;

    public static CRFSegmenterInstance getInstance() {
        if (instance == null) {
            synchronized (CRFSegmenterInstance.class) {
                if (instance == null) {//二次检查
                    instance = new CRFSegmenterInstance();
                }
            }
        }
        return instance;
    }

    private final CRFSegmenter segmenter;

    private CRFSegmenterInstance() {
        Path pat=Paths.get(AccessController.doPrivileged((PrivilegedAction<String>) () -> HanlpPath.CRFCWSModelPath)
        ).toAbsolutePath();
        if (FileSystemUtils.exists(pat)) {
            segmenter = AccessController.doPrivileged((PrivilegedAction<CRFSegmenter>) () -> {
                try {
                    return new CRFSegmenter(HanlpPath.CRFCWSModelPath);
                } catch (IOException e) {
                    logger.error(() -> new ParameterizedMessage("load crf cws model from [{}] error", HanlpPath.CRFCWSModelPath), e);
                    return null;
                }
            });
        } else {
            logger.warn("can not find crf cws model from [{}]", HanlpPath.CRFCWSModelPath);
            segmenter = null;
        }
    }

    public CRFSegmenter getSegmenter() {
        return segmenter;
    }
}
