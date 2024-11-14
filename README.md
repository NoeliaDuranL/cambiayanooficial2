#Proyecto CambiaYa, v1

### Explicación del contenido:

1. **Título**: APP MOVIL "CAMBIAYA"
2. **Configuración Previa**:

**Configuración de la IP en el `ApiClient`**: 
Instrucciones para cambiar la IP del servidor en el código Kotlin de la aplicación. 
Busca la siguiente línea:

     ```kotlin
       private const val BASE_URL = "http://192.168.43.237:8000/api/"
          
     ```
   
y cambia por la ip de tu maquina fisica si estas ejecutando en un dispositivo movil fisico
**Ejemplo:  http://<tu=ip>:8000/api/**
manten el puerto en :8000
### Para emuladores:

Utiliza la IP 10.0.2.2 (que es la forma en que el emulador se conecta a tu máquina local), el puerto se mantiene

**Configuración de la IP en el archivo `network_security_config.xml`**:

Instrucciones para actualizar la IP en la configuración de seguridad de la red.
También necesitas actualizar la IP en el archivo de configuración de seguridad de red para permitir el tráfico claro hacia tu servidor. Este archivo se encuentra en res/xml/network_security_config.xml.

Abre el archivo y busca la siguiente línea:
```xml
  <domain includeSubdomains="true">192.168.43.237</domain><!-- Reemplaza con la IP de tu servidor -->
```

