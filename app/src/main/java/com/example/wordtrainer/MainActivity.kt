package com.example.wordtrainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.io.BufferedReader

data class Word(val text: String)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val words = try {
            assets.open("words.txt").bufferedReader().use(
                BufferedReader::readLines
            )
                .map { it.trim() }
                .filter {
                    it.isNotEmpty() &&
                    !it.startsWith("#")
                }
                .map { Word(it) }
        } catch (e: Exception) {
            emptyList()
        }

        setContent {
            MaterialTheme {
                WordTrainer(words)
            }
        }
    }
}

@Composable
fun WordTrainer(words: List<Word>) {

    var query by remember {
        mutableStateOf("")
    }

    var currentIndex by remember {
        mutableIntStateOf(0)
    }

    val filteredWords = remember(words, query) {
        if (query.isBlank()) {
            words
        } else {
            words.filter {
                it.text.contains(
                    query.trim(),
                    ignoreCase = true
                )
            }
        }
    }

    LaunchedEffect(filteredWords.size) {
        if (filteredWords.isEmpty()) {
            currentIndex = 0
        } else {
            currentIndex =
                currentIndex.coerceIn(0, filteredWords.lastIndex)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("1100 Word Trainer")
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "آموزش واژگان",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    currentIndex = 0
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("جستجوی واژه")
                },
                singleLine = true
            )

            if (filteredWords.isEmpty()) {

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "واژه‌ای پیدا نشد.",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "فایل words.txt را بررسی کنید."
                        )
                    }
                }

            } else {

                val word = filteredWords[
                    currentIndex.coerceIn(
                        0,
                        filteredWords.lastIndex
                    )
                ]

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Text(
                            text = "${currentIndex + 1} / ${filteredWords.size}",
                            style = MaterialTheme.typography.labelLarge
                        )

                        Text(
                            text = word.text,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "واژه را مرور کنید و سپس به واژه بعدی بروید.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Button(
                        enabled = currentIndex > 0,
                        onClick = {
                            currentIndex--
                        }
                    ) {
                        Text("قبلی")
                    }

                    Button(
                        enabled = currentIndex < filteredWords.lastIndex,
                        onClick = {
                            currentIndex++
                        }
                    ) {
                        Text("بعدی")
                    }
                }
            }

            HorizontalDivider()

            Text(
                text = "فهرست واژه‌ها",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                items(filteredWords) { word ->

                    Text(
                        text = word.text,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 8.dp,
                                horizontal = 4.dp
                            )
                    )
                }
            }
        }
    }
}
