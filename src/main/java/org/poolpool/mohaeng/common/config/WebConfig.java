package org.poolpool.mohaeng.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    // 💡 방금 보여주신 그 프로퍼티를 자동으로 가져옵니다!
    private final UploadProperties uploadProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 프론트엔드에서 "/upload_files/..." 로 주소를 입력하면
        registry.addResourceHandler("/upload_files/**")
                // uploadProperties에 적어둔 물리적 경로(C:/upload_files/)로 연결해 줍니다!
                .addResourceLocations("file:///" + uploadProperties.uploadDir() + "/");
    }
}