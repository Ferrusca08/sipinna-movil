package com.sipinna.atizapan.reportes.view.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sipinna.atizapan.reportes.view.ConfirmacionScreen
import com.sipinna.atizapan.reportes.view.ConsultarEstatusScreen
import com.sipinna.atizapan.reportes.view.EvidenciaScreen
import com.sipinna.atizapan.reportes.view.HomeScreen
import com.sipinna.atizapan.reportes.view.InfoScreen
import com.sipinna.atizapan.reportes.view.LoginScreen
import com.sipinna.atizapan.reportes.view.MisReportesScreen
import com.sipinna.atizapan.reportes.view.NuevoReporteScreen
import com.sipinna.atizapan.reportes.view.RegistroScreen

/** Rutas de la app. El token viaja como argumento para no guardar estado global. */
object Rutas {
    const val HOME = "home"
    const val NUEVO_REPORTE = "nuevo_reporte"
    const val CONFIRMACION = "confirmacion/{reporteId}"
    const val EVIDENCIA = "evidencia/{reporteId}"
    const val CONSULTA = "consulta?token={token}"
    const val INFO = "info"
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val MIS_REPORTES = "mis_reportes"

    fun confirmacion(reporteId: String) = "confirmacion/$reporteId"
    fun evidencia(reporteId: String) = "evidencia/$reporteId"
    fun consulta(token: String = "") = "consulta?token=$token"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Rutas.HOME) {

        composable(Rutas.HOME) {
            HomeScreen(
                alReportar = { navController.navigate(Rutas.NUEVO_REPORTE) },
                alConsultar = { navController.navigate(Rutas.consulta()) },
                alVerInfo = { navController.navigate(Rutas.INFO) },
                alIrAMisReportes = { navController.navigate(Rutas.MIS_REPORTES) },
                alIrALogin = { navController.navigate(Rutas.LOGIN) },
            )
        }

        composable(Rutas.LOGIN) {
            LoginScreen(
                alRegresar = { navController.popBackStack() },
                alIrARegistro = {
                    navController.navigate(Rutas.REGISTRO) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                alIniciarSesionExitoso = {
                    navController.navigate(Rutas.MIS_REPORTES) {
                        popUpTo(Rutas.HOME)
                    }
                },
            )
        }

        composable(Rutas.REGISTRO) {
            RegistroScreen(
                alRegresar = { navController.popBackStack() },
                alIrALogin = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.REGISTRO) { inclusive = true }
                    }
                },
                alRegistroExitoso = {
                    navController.navigate(Rutas.MIS_REPORTES) {
                        popUpTo(Rutas.HOME)
                    }
                },
            )
        }

        composable(Rutas.MIS_REPORTES) {
            MisReportesScreen(
                alRegresar = { navController.popBackStack() },
                alIrALogin = { navController.navigate(Rutas.LOGIN) },
                alIrARegistro = { navController.navigate(Rutas.REGISTRO) },
                alCrearReporte = { navController.navigate(Rutas.NUEVO_REPORTE) },
            )
        }

        composable(Rutas.NUEVO_REPORTE) {
            NuevoReporteScreen(
                alRegresar = { navController.popBackStack() },
                alEnviar = { reporteId ->
                    navController.navigate(Rutas.confirmacion(reporteId)) {
                        // El formulario ya no debe quedar en el back stack.
                        popUpTo(Rutas.HOME)
                    }
                },
            )
        }

        composable(
            route = Rutas.CONFIRMACION,
            arguments = listOf(navArgument("reporteId") { type = NavType.StringType }),
        ) { entrada ->
            val reporteId = entrada.arguments?.getString("reporteId").orEmpty()
            ConfirmacionScreen(
                reporteId = reporteId,
                alAgregarFotos = { navController.navigate(Rutas.evidencia(reporteId)) },
                alVolverAlInicio = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.HOME) { inclusive = true }
                    }
                },
            )
        }

        composable(
            route = Rutas.EVIDENCIA,
            arguments = listOf(navArgument("reporteId") { type = NavType.StringType }),
        ) {
            EvidenciaScreen(
                alTerminar = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.HOME) { inclusive = true }
                    }
                },
                alRegresar = { navController.popBackStack() },
            )
        }

        composable(
            route = Rutas.CONSULTA,
            arguments = listOf(
                navArgument("token") {
                    type = NavType.StringType
                    defaultValue = ""
                },
            ),
        ) {
            ConsultarEstatusScreen(alRegresar = { navController.popBackStack() })
        }

        composable(Rutas.INFO) {
            InfoScreen(alRegresar = { navController.popBackStack() })
        }
    }
}
