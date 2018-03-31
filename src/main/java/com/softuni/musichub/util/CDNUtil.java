package com.softuni.musichub.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.Url;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class CDNUtil {

    private static final String RESOURCE_TYPE_KEY = "resource_type";

    private static final String FOLDER_KEY = "folder";

    private static final String CLOUD_NAME_KEY = "cloud_name";

    private static final String CLOUD_NAME_VALUE = "musichub-ld";

    private static final String VERSION_KEY = "version";

    private static final String API_KEY = "api_key";

    private static final String API_KEY_VALUE = "536486612627791";

    private static final String API_SECRET_KEY = "api_secret";

    private static final String API_SECRET_VALUE = "uJAGCgJLCkBMKWz8V--pyR3F-Is";

    public static final String VIDEO_RESOURCE_TYPE = "video";

    public static final String IMAGE_RESOURCE_TYPE = "image";

    public static final String PUBLIC_ID_KEY = "public_id";

    public static final String SONGS_FOLDER = "songs";

    public static final String IMAGES_FOLDER = "images";

    private static final String ATTACHMENT_FLAG = "attachment";

    private Cloudinary cloudinary;

    public CDNUtil() {
        this.configCloudinary();
    }

    private void configCloudinary() {
        Map configMap = ObjectUtils.asMap(
                CLOUD_NAME_KEY, CLOUD_NAME_VALUE,
                API_KEY, API_KEY_VALUE,
                API_SECRET_KEY, API_SECRET_VALUE
        );

        this.cloudinary = new Cloudinary(configMap);
    }

    @Async
    public CompletableFuture<Map> upload(File file, String resourceType,
                                         String folderToUpload) throws IOException {
        Map fileConfig = ObjectUtils.asMap(
                RESOURCE_TYPE_KEY, resourceType,
                FOLDER_KEY, folderToUpload);
        Map uploadResult = this.cloudinary.uploader().upload(file, fileConfig);
        return CompletableFuture.completedFuture(uploadResult);
    }

    public String getResourceFullUrl(String partialUrl, String resourceType) {
        String resourceFullUrl = this.cloudinary.url()
                .resourceType(resourceType).generate(partialUrl);
        return resourceFullUrl;
    }

    public String getResourceDownloadUrl(String partialUrl, String resourceType) throws Exception {
        Transformation attachmentFlag = new Transformation().flags(ATTACHMENT_FLAG);
        Url url = this.cloudinary.url().transformation(attachmentFlag);
        String downloadUrl = url.resourceType(resourceType).generate(partialUrl);
        return downloadUrl;
    }

    public void deleteResource(String partialUrl) throws Exception {
        this.cloudinary.api().deleteResources(Arrays.asList(partialUrl),
                ObjectUtils.asMap(RESOURCE_TYPE_KEY, VIDEO_RESOURCE_TYPE));
    }
}