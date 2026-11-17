# GestorPharma

Aplicación de farmacia para el segundo corte de Aplicativos Móviles. Está hecha con Kotlin, Jetpack Compose, Material 3 y Room. Permite administrar el inventario, consultar el catálogo y guardar un carrito de compras sin conexión a Internet.

## Iniciar sesión

Cuenta de demostración:

- Usuario: `admin`
- Contraseña: `Farmacia123!`

La cuenta se crea si todavía no existe. La contraseña se guarda con PBKDF2 y una sal aleatoria. La sesión se conserva al girar la pantalla; al cerrar el proceso se debe iniciar sesión otra vez. En **Perfil > Cerrar sesión** se sale de la cuenta sin borrar los productos ni el carrito.

## Funciones

| Función | Cómo probarla |
|---|---|
| Registrar medicamentos | Entrar a **Inventario > Nuevo Producto**, completar nombre, descripción, precio y categoría, y pulsar **Guardar**. El precio se escribe en pesos, sin puntos ni símbolo. |
| Consultar el catálogo | Abrir **Inicio** o **Inventario**. Los productos registrados aparecen en las dos pantallas. |
| Editar medicamentos | En Inventario, pulsar el lápiz de un producto, cambiar sus datos y guardar. El cambio también se refleja en Inicio y Carrito. |
| Eliminar medicamentos | Pulsar la papelera y confirmar. Si el producto estaba en el carrito, también se retira de allí. |
| Consultar estadísticas | Revisar **Resumen de Inventario**. Los contadores de productos, ofertas y categorías cambian al modificar el catálogo. |

En **Inicio** se puede buscar por nombre o descripción y combinar la búsqueda con una categoría. Al tocar de nuevo la categoría seleccionada se quita el filtro.

El **Carrito** permite agregar productos, cambiar cantidades, vaciarlo con confirmación y registrar un pedido de demostración. No se realizan cobros. El historial y la dirección de entrega se consultan desde **Perfil**.

Los formularios comprueban nombre obligatorio, precio entero positivo y categoría válida. El carrito admite hasta 999 unidades por producto; al bajar de una unidad se retira del carrito.

## Organización del proyecto

Se sigue el flujo **Room → Repository → ViewModel → pantallas Compose**. Las pantallas reciben los datos y las acciones por parámetros. Las dependencias se crean en `MainActivity` mediante una Factory, sin un framework adicional.

- `model`: `Medicamento`, `Categoria`, `Usuario`, `Pedido` y las rutas de navegación en `Rutas`.
- `data/local`: entidades, DAO y base de datos.
- `data/repository`: operaciones del catálogo, carrito, usuarios y pedidos.
- `viewmodel`: estado de la aplicación con StateFlow y operaciones con corrutinas.
- `ui/screens`: Inicio, Inventario, Carrito, Perfil e Inicio de sesión.
- `ui/components`: `TarjetaProducto`, `TarjetaCategoria`, `ResumenInventario` y `FormularioMedicamento`.
- `ui/util`: `IconosCategoria`, que asigna un ícono según el tipo de producto.
- `util`: manejo de contraseñas.

Room guarda medicamentos, elementos del carrito, usuarios y pedidos. El carrito se relaciona con los medicamentos mediante `medicamentoId`. También se guarda un indicador interno para cargar los productos de ejemplo una sola vez.

La base está en la versión 4 y tiene migraciones desde las versiones 1 y 3. Los campos antiguos de imagen se conservan para poder actualizar instalaciones anteriores, aunque la interfaz ya usa íconos por categoría.

## Comprobar la persistencia

1. Registrar dos productos de prueba, editar el precio de uno y agregarlo al carrito.
2. Eliminar el otro producto y confirmar.
3. Forzar la detención de la aplicación desde los ajustes de Android y volver a abrirla.
4. Iniciar sesión y comprobar que siguen el producto editado y las cantidades del carrito, y que el producto eliminado no reaparece.

Si se elimina todo el inventario y se reinicia la app, debe seguir vacío. Los productos de ejemplo no se vuelven a cargar.

Para probar una actualización se instala el nuevo APK con la misma firma y sin desinstalar la versión anterior. Desinstalar o borrar el almacenamiento elimina los datos y no sirve para comprobar esta prueba.

## Compilar e instalar

Abrir el proyecto en Android Studio y sincronizar Gradle. Se necesita el SDK configurado en el proyecto y un dispositivo con Android 7.0 (API 24) o superior.

Desde la raíz del proyecto, en PowerShell:

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

El APK se genera en `app/build/outputs/apk/debug/app-debug.apk`.

Para instalarlo con ADB:

```powershell
adb install -r .\app\build\outputs\apk\debug\app-debug.apk
adb shell am start -n com.example.sistemafarmacia/.MainActivity
```

Las pruebas del dispositivo se ejecutan con un teléfono o emulador conectado:

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest
```

Se incluyen pruebas de contraseñas, migraciones, persistencia, cantidades del carrito e inicio de sesión. La revisión con TalkBack y en otras versiones de Android se puede hacer siguiendo los mismos recorridos de las pantallas.

## Referencias y entrega

- [TaskFlow del profesor](https://github.com/JCEspitia/TaskFlow): Room, Repository, ViewModel, Factory y observación de datos.
- [ElectroStoreApp del profesor](https://github.com/JCEspitia/ElectroStoreApp): catálogo, categorías, carrito y navegación con Compose.
- [Repositorio de SistemaFarmacia](https://github.com/Kratos0996/SistemaFarmacia).

La entrega incluye el APK instalable, el enlace al repositorio y esta guía para localizar y probar las funciones.
