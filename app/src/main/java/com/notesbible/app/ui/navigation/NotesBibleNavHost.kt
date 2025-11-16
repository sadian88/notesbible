package com.notesbible.app.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.notesbible.app.data.BibleRepository
import com.notesbible.app.ui.note.HandwrittenNoteRoute
import com.notesbible.app.ui.reader.ReaderRoute
import com.notesbible.app.ui.version.VersionListRoute

@Composable
fun NotesBibleNavHost(
    repository: BibleRepository,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Versions.route,
        modifier = modifier
    ) {
        composable(Screen.Versions.route) {
            VersionListRoute(
                repository = repository,
                onReadVersion = { versionId ->
                    navController.navigate(Screen.Reader.createRoute(versionId))
                }
            )
        }
        composable(
            route = Screen.Reader.route,
            arguments = listOf(navArgument(Screen.Reader.VERSION_ARG) { type = NavType.StringType })
        ) { backStackEntry ->
            val versionId = backStackEntry.arguments?.getString(Screen.Reader.VERSION_ARG).orEmpty()
            ReaderRoute(
                repository = repository,
                versionId = versionId,
                onBack = { navController.popBackStack() },
                onOpenNotes = { book, chapter ->
                    navController.navigate(Screen.Note.createRoute(versionId, book, chapter))
                }
            )
        }
        composable(
            route = Screen.Note.route,
            arguments = listOf(
                navArgument(Screen.Note.VERSION_ARG) { type = NavType.StringType },
                navArgument(Screen.Note.BOOK_ARG) { type = NavType.StringType },
                navArgument(Screen.Note.CHAPTER_ARG) { type = NavType.IntType },
                navArgument(Screen.Note.VERSE_ARG) { type = NavType.IntType; defaultValue = 0 }
            )
        ) { backStackEntry ->
            val versionId = backStackEntry.arguments?.getString(Screen.Note.VERSION_ARG).orEmpty()
            val bookArg = backStackEntry.arguments?.getString(Screen.Note.BOOK_ARG).orEmpty()
            val chapter = backStackEntry.arguments?.getInt(Screen.Note.CHAPTER_ARG) ?: 1
            val verse = backStackEntry.arguments?.getInt(Screen.Note.VERSE_ARG) ?: 0
            val decodedBook = Uri.decode(bookArg)
            HandwrittenNoteRoute(
                repository = repository,
                versionId = versionId,
                book = decodedBook,
                chapter = chapter,
                verse = verse,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

sealed class Screen(val route: String) {
    data object Versions : Screen("versions")

    data object Reader : Screen("reader/{versionId}") {
        const val VERSION_ARG = "versionId"
        fun createRoute(versionId: String) = "reader/$versionId"
    }

    data object Note : Screen("note/{versionId}/{book}/{chapter}/{verse}") {
        const val VERSION_ARG = "versionId"
        const val BOOK_ARG = "book"
        const val CHAPTER_ARG = "chapter"
        const val VERSE_ARG = "verse"

        fun createRoute(versionId: String, book: String, chapter: Int, verse: Int = 0): String {
            val encodedBook = Uri.encode(book)
            return "note/$versionId/$encodedBook/$chapter/$verse"
        }
    }
}
