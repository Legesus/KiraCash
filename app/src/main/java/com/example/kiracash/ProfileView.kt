package com.example.kiracash

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout

class ProfileView : AppCompatActivity () {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(R.layout.view_profile)

        setContent{
            profile()
        }
    }

    @Preview
    @Composable
    private fun profile() {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = Color(android.graphics.Color.parseColor("#f2f1f6"))),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ConstraintLayout(
                Modifier
                    .height(250.dp)
                    .background(color = Color(android.graphics.Color.parseColor("#32357a")))) {
                    val (topImg,profile, title, back, pen) = createRefs()

                Image(painterResource(id = R.drawable.arc_3), contentDescription = null,
                    Modifier
                        .constrainAs(topImg) {
                            bottom.linkTo(parent.bottom)
                        }
                        .fillMaxWidth())
            }
        }
    }
}