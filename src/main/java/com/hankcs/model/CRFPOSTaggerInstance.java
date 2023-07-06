package com.hankcs.model;

import com.hankcs.cfg.HanlpPath;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.model.crf.CRFPOSTagger;
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
public class CRFPOSTaggerInstance {

    private static final Logger logger = LogManager.getLogger(CRFPOSTaggerInstance.class);

    private static volatile CRFPOSTaggerInstance instance = null;

    public static CRFPOSTaggerInstance getInstance() {
        if (instance == null) {
            synchronized (CRFPOSTaggerInstance.class) {
                if (instance == null) {//二次检查
                    instance = new CRFPOSTaggerInstance();
                }
            }
        }
        return instance;
    }

    private final CRFPOSTagger tagger;

    private CRFPOSTaggerInstance() {
        Path path = Paths.get(AccessController.doPrivileged((PrivilegedAction<String>) () -> HanlpPath.CRFPOSModelPath)
        ).toAbsolutePath();
        if (FileSystemUtils.exists(path)) {
            tagger = AccessController.doPrivileged((PrivilegedAction<CRFPOSTagger>) () -> {
                try {
                    return new CRFPOSTagger(HanlpPath.CRFPOSModelPath);
                } catch (IOException e) {
                    logger.error(() -> new ParameterizedMessage("load crf pos model from [{}] error", HanlpPath.CRFPOSModelPath), e);
                    return null;
                }
            });
        } else {
            logger.warn("can not find crf pos model from [{}]", HanlpPath.CRFPOSModelPath);
            tagger = null;
        }
    }

    public CRFPOSTagger getTagger() {
        return tagger;
    }
}
