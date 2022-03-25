package com.naniak.whatsupdog

import android.R
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.naniak.whatsupdog.databinding.FragmentDogListBinding


class DogListFragment : Fragment() {

    private var binding: FragmentDogListBinding? = null
    private lateinit var viewModel: DogsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDogListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       activity?.let {
           viewModel = ViewModelProvider(it).get(DogsViewModel::class.java)
           viewModel.command = MutableLiveData()
       }
        viewModel.getRandomDogs()

        setupObservable()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setupObservable() {

        viewModel.onSuccessDogRandom.observe(viewLifecycleOwner) {
            binding?.run {
                 Glide.with(requireActivity()).load(it.message).into(dogApi)
                button.setOnClickListener{
                    viewModel.getRandomDogs()
                }
            }

        }

    }

}