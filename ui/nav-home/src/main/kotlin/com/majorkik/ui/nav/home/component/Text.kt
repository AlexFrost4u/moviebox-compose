package com.majorkik.ui.nav.home.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.majorkik.common.percentOf
import com.majorkik.core.ui.theme.MovieBoxTheme

@Composable
internal fun TitleText(@StringRes text: Int) {
    Text(
        text = stringResource(id = text),
        style = MovieBoxTheme.typography.h3,
        color = MovieBoxTheme.colors.backgroundReverse,
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )
}

@Composable
internal fun PercentVoteAverageText(voteAverage: Double, modifier: Modifier = Modifier) {
    Text(
        "${voteAverage.percentOf(from = 10)}%",
        style = MovieBoxTheme.typography.titleSmall,
        color = MovieBoxTheme.colors.text.white,
        modifier = modifier
            .padding(8.dp)
            .clip(CircleShape)
            .background(MovieBoxTheme.colors.backgroundDark50)
            .padding(vertical = 4.dp, horizontal = 8.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun TextPreview() {
    MovieBoxTheme {
        Column {
            TitleText(text = com.majorkik.core.ui.R.string.trending_movies)

            PercentVoteAverageText(voteAverage = 7.39)
        }
    }
}
