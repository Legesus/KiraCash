package com.example.kiracash

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.material.Text
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
                    .background(color = Color(android.graphics.Color.parseColor("#32357a")))
            ) {
                val (topImg, profile, title, back, pen) = createRefs()

                Image(painterResource(id = R.drawable.arc_3), contentDescription = null,
                    Modifier
                        .constrainAs(topImg) {
                            bottom.linkTo(parent.bottom)
                        }
                        .fillMaxWidth())

                Image(painterResource(id = R.drawable.user_2), contentDescription = null,
                    Modifier
                        .constrainAs(profile) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(topImg.bottom)
                        }
                        .fillMaxWidth())

                Text(text = "Profile",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 30.sp
                    ),
                    modifier = Modifier
                        .constrainAs(title) {
                            top.linkTo(parent.top, margin = 32.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        })

                Image(painterResource(id = R.drawable.back), contentDescription = null,
                    Modifier
                        .constrainAs(back) {
                            top.linkTo(parent.top, margin = 24.dp)
                            start.linkTo(parent.start, margin = 24.dp)
                        })

                Image(painterResource(id = R.drawable.write), contentDescription = null,
                    Modifier
                        .constrainAs(pen) {
                            top.linkTo(profile.top)
                            start.linkTo(profile.end)
                        })
            }
            Text(
                text = "Alex Flores",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp),
                color = Color(android.graphics.Color.parseColor("#32357a"))
            )

            Text(
                text = "alex@gmail.com",
                fontSize = 18.sp,
                color = Color(android.graphics.Color.parseColor("#747679"))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, end = 32.dp, top = 32.dp, bottom = 10.dp)
                    .height(55.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
            Column (modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                Image(painter = painterResource(id = R.drawable.btn_1), contentDescription = null,
                    modifier = Modifier.padding(end=5.dp)
                        .clickable { /*TODO*/ })
            }
            }
        }
    }
}