package com.kominfotabalong.simasganteng.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kominfotabalong.simasganteng.R
import com.kominfotabalong.simasganteng.data.model.StatistikResponse

@Composable
fun ItemStatistik(
    modifier: Modifier = Modifier,
    dataStatistik: StatistikResponse,
) {
    Card(
        modifier = modifier.fillMaxWidth(0.48f),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(20.dp)
    )
    {
        Row(
            modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(modifier = modifier.weight(1f)) {
                Text(
                    text = dataStatistik.status,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold)
                )
                Text(
                    text = dataStatistik.jumlah.toString(),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Icon(
                painter = painterResource(R.drawable.stats),
                contentDescription = "",
                modifier = modifier.size(24.dp)
            )
        }
    }
}