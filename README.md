# 🏦 Banking API & Management System

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)](https://www.docker.com/)

Sistema integral para la gestión de clientes, cuentas bancarias y movimientos financieros, con lógica de negocio avanzada para integridad de saldos y límites transaccionales.

## 📋 Tabla de Contenidos
- [Arquitectura](#arquitectura)
- [Reglas de Negocio Implementadas](#reglas-de-negocio-implementadas)
- [Estructura de Datos](#estructura-de-datos)
- [Instalación y Despliegue](#instalación-y-despliegue)
- [Endpoints Principales](#endpoints-principales)

## 🏗 Arquitectura
El proyecto implementa una arquitectura limpia y escalable:
- **Backend:** Spring Boot con **Spring Data JPA**. Utiliza Herencia de tablas (`JOINED`) para la relación Persona-Cliente.
- **Base de Datos:** PostgreSQL en contenedor Docker, inicializada automáticamente mediante scripts SQL.
- **Validaciones:** Bean Validation (JSR 380) para integridad de datos a nivel de API.
- **Contenerización:** Despliegue orquestado con Docker Compose.



## ⚖️ Reglas de Negocio Implementadas
1. **Validación de Saldo:** No se permiten retiros que excedan el saldo disponible. Respuesta: `400 Bad Request` - "Saldo no disponible".
2. **Límite Diario:** Cupo máximo de retiro diario de **$1,000.00** por cuenta. Respuesta: `400 Bad Request` - "Cupo diario Excedido".
3. **Integridad de Herencia:** Los clientes heredan atributos de Persona, garantizando unicidad de identificación y corrección de datos personales.

## 🚀 Instalación y Despliegue

### Requisitos Previos
- Docker y Docker Compose instalados.
- Puerto `8080` y `5432` disponibles.

### Despliegue Automático (Recomendado)
Para asegurar una instalación limpia (borrando volúmenes previos y cargando el script SQL de inicialización), ejecute:

```bash
# Limpiar entorno previo y volúmenes de base de datos
docker-compose down -v

# Construir y levantar servicios
docker-compose up --build