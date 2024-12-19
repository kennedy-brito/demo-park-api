package com.kennedy.demo_park_api.servicies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class JasperService {

    private final ResourceLoader resourceLoader;
    private final DataSource dataSource;

    private Map<String, Object> params = new HashMap<>();

    private static final String JASPER_DIR = "classpath:reports/";

    public void addParams(String key, Object value){
        params.put("IMAGE_DIR", JASPER_DIR);
        params.put("REPORT_LOCALE", new Locale("pt", "BR"));
        params.put(key, value);
    }

    public byte[] generatePdf(){

        byte[] bytes = null;

        try{
            Resource resource = resourceLoader.getResource(JASPER_DIR.concat("demo_park.jasper"));
            InputStream stream = resource.getInputStream();
            JasperPrint print = JasperFillManager.fillReport(stream, params, dataSource.getConnection());
            bytes = JasperExportManager.exportReportToPdf(print);
        } catch (IOException | SQLException | JRException e) {
            log.error("Jasper Reports::: " + e.getCause());
            throw new RuntimeException(e);
        }

        return bytes;
    }

}
