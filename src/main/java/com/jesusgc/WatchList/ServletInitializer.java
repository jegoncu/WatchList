package com.jesusgc.WatchList;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Inicializador de servlet para despliegue en contenedores de aplicaciones web.
 * Permite que la aplicación Spring Boot se ejecute en servidores de
 * aplicaciones tradicionales.
 *
 * @author Jesús González Cuenca
 */
public class ServletInitializer extends SpringBootServletInitializer {

  /**
   * Configura la aplicación Spring Boot para su despliegue como WAR.
   * Define la clase principal de la aplicación que contiene el método main.
   *
   * @param application Constructor de la aplicación Spring Boot
   * @return Constructor configurado con las fuentes de la aplicación
   */
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(WatchListApplication.class);
  }

}
