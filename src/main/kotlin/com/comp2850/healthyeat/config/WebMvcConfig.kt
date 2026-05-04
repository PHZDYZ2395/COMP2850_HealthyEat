/**
 * WebMvcConfig configures web MVC settings for serving static resources.
 * Maps the root path to login.html and configures resource handlers.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig : WebMvcConfigurer {

    /**
     * Configure default view for root path.
     * @param registry view controller registry
     */
    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/").setViewName("forward:/index.html")
    }

    /**
     * Configure resource handlers for static files.
     * @param registry resource handler registry
     */
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
    }
}
