package com.jesusgc.WatchList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación WatchList.
 * Punto de entrada para la aplicación Spring Boot que gestiona listas de
 * películas y series.
 *
 * @author Jesús González Cuenca
 */
@SpringBootApplication
public class WatchListApplication {

  /**
   * Método principal que inicia la aplicación Spring Boot.
   * Configura automáticamente el contexto de la aplicación y arranca el servidor
   * embebido.
   *
   * @param args Argumentos de línea de comandos pasados a la aplicación
   */
  public static void main(String[] args) {
    SpringApplication.run(WatchListApplication.class, args);
  }

}
