# 🏦 Banking API & Management System

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)](https://www.docker.com/)

Sistema integral para la gestión de clientes, cuentas bancarias y movimientos financieros, con lógica de negocio avanzada para integridad de saldos y límites transaccionales.

## 📋 Tabla de Contenidos
- [Arquitectura](#arquitectura)
- [Reglas de Negocio Implementadas](#reglas-de-negocio-implementadas)
- [Importación de Datos](#importación-de-datos)
- [Instalación y Despliegue](#instalación-y-despliegue)
- [Endpoints Principales](#endpoints-principales)

## 🏗 Arquitectura
El proyecto implementa una arquitectura limpia y escalable:
- **Backend:** Spring Boot con **Spring Data JPA**. Utiliza Herencia de tablas mediante la estrategia `JOINED` para la relación Persona-Cliente.
- **Base de Datos:** PostgreSQL 15 en contenedor Docker, configurada para inicialización automática.
- **Validaciones:** Bean Validation (JSR 380) para integridad de datos a nivel de API.
- **Contenerización:** Despliegue orquestado con Docker Compose.

[Image of a logical layered architecture diagram showing Controller, Service, and Repository layers]

## ⚖️ Reglas de Negocio Implementadas
1. **Validación de Saldo:** No se permiten retiros que excedan el saldo disponible. Respuesta: `400 Bad Request` - "Saldo no disponible".
2. **Límite Diario:** Cupo máximo de retiro diario de **$1,000.00** por cuenta. Respuesta: `400 Bad Request` - "Cupo diario Excedido".
3. **Integridad de Herencia:** Los clientes heredan atributos de Persona, garantizando unicidad de identificación y corrección de datos personales.

## 📂 Importación de Datos
El sistema está configurado para automatizar la creación de la base de datos. Se incluye el archivo **`BaseDatos.sql`** en la raíz del proyecto, el cual es procesado por Docker al iniciar.

### Datos de prueba cargados inicialmente:
| Cliente | ID Cliente | Cuenta | Saldo Inicial |
| :--- | :--- | :--- | :--- |
| Jose Lema | C001 | 478758 | $2,000.00 |
| Marian Arandi | C002 | 225487 | $100.00 |
| Juan Osorio | C003 | 495878 | $0.00 |

## 🚀 Instalación y Despliegue

### Requisitos Previos
- Docker y Docker Compose instalados.
- Puertos `8080` y `5432` libres.

### Pasos para el Despliegue
Para asegurar una instalación limpia (borrando volúmenes previos y cargando el script SQL de inicialización), ejecute los siguientes comandos en la raíz del proyecto:

1. **Limpiar y Levantar:**
   ```bash
   docker-compose down -v && docker-compose up --build