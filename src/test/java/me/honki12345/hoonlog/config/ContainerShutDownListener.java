package me.honki12345.hoonlog.config;

import static me.honki12345.hoonlog.domain.util.FileUtils.IMAGE_LOCATION;
import static me.honki12345.hoonlog.domain.util.FileUtils.UPLOAD_LOCATION;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import java.io.File;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ContainerShutDownListener {

    @Bean
    ServletListenerRegistrationBean<ServletContextListener> servletListener() {
        ServletListenerRegistrationBean<ServletContextListener> srb
            = new ServletListenerRegistrationBean<>();
        srb.setListener(new TestServletContextListener());
        return srb;
    }

    static class TestServletContextListener implements ServletContextListener {

        @Override
        public void contextDestroyed(ServletContextEvent sce) {
            File imageFolder = new File(IMAGE_LOCATION);
            if (imageFolder.exists()) {
                deleteDir(imageFolder);
            }

            File uploadFolder = new File(UPLOAD_LOCATION);
            if (uploadFolder.exists()) {
                deleteDir(uploadFolder);
            }
        }

        private void deleteDir(File file) {
            String[] entries = file.list();
            for (String s : entries) {
                File currentFile = new File(file.getPath(), s);
                currentFile.delete();
            }
        }
    }
}
