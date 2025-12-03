package pl.polsatgranie.dailycytato.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pl.polsatgranie.dailycytato.databinding.FragmentHomeBinding
import pl.polsatgranie.dailycytato.ui.main.MainViewModel

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))
            .get(MainViewModel::class.java)
        
        setupObservers()
        setupListeners()
        
        lifecycleScope.launch {
            viewModel.loadTodaysQuote()
        }
    }
    
    private fun setupObservers() {
        viewModel.currentQuote.observe(viewLifecycleOwner) { quote ->
            quote?.let {
                binding.tvQuoteContent.text = it.content
                binding.tvQuoteAuthor.text = "— ${it.author}"
                binding.tvError.visibility = View.GONE
                binding.quoteCard.visibility = View.VISIBLE
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                binding.tvError.text = it
                binding.tvError.visibility = View.VISIBLE
                binding.quoteCard.visibility = View.GONE
            }
        }
    }
    
    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.loadNewQuote()
            }
        }
        
        binding.btnRefresh.setOnClickListener {
            lifecycleScope.launch {
                viewModel.loadNewQuote()
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
