package pl.polsatgranie.dailycytato.ui.preferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import pl.polsatgranie.dailycytato.databinding.FragmentPreferencesBinding
import pl.polsatgranie.dailycytato.ui.preferences.adapter.CategoryAdapter

class PreferencesFragment : Fragment() {
    
    private var _binding: FragmentPreferencesBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var viewModel: PreferencesViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreferencesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        auth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))
            .get(PreferencesViewModel::class.java)
        
        setupRecyclerView()
        setupObservers()
        setupListeners()
        loadPreferences()
    }
    
    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter { category ->
            viewModel.toggleCategory(category)
        }
        
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }
    }
    
    private fun setupObservers() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
        }
        
        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Preferencje zapisane", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Błąd zapisu preferencji", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            lifecycleScope.launch {
                viewModel.savePreferences()
            }
        }
    }
    
    private fun loadPreferences() {
        auth.currentUser?.let { user ->
            viewModel.loadPreferences(user.uid)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
