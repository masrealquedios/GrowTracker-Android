package me.anon.grow3.ui.logs.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.anon.grow3.data.model.Diary
import me.anon.grow3.data.model.Environment
import me.anon.grow3.databinding.CardWaterLogBinding
import me.anon.grow3.view.model.Card

class EnvironmentLogCard : Card<CardWaterLogBinding>
{
	private lateinit var diary: Diary
	private lateinit var log: Environment

	constructor() : super(null)
	constructor(diary: Diary, log: Environment, title: String? = null) : super(title)
	{
		this.diary = diary
		this.log = log
	}

	override fun createView(inflater: LayoutInflater, parent: ViewGroup): CardWaterLogBinding
		= CardWaterLogBinding.inflate(inflater, parent, false)
	override fun bindView(view: View): CardWaterLogBinding = CardWaterLogBinding.bind(view)

	override fun bind(view: CardWaterLogBinding)
	{
		view.stubCardHeader.header.text = log.summary()
	}
}
