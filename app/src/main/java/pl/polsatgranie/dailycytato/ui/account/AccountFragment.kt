package pl.polsatgranie.dailycytato.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import pl.polsatgranie.dailycytato.databinding.FragmentAccountBinding
import pl.polsatgranie.dailycytato.ui.login.LoginActivity

class AccountFragment : Fragment() {
    
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: AccountViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        auth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))
            .get(AccountViewModel::class.java)
        
        setupObservers()
        setupListeners()
        loadUserData()
    }
    
    private fun setupObservers() {
        viewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvUserName.text = it.displayName
                binding.tvUserEmail.text = it.email
                
                if (it.preferredCategories.isNotEmpty()) {
                    binding.tvSelectedCategories.text = it.preferredCategories.joinToString(", ")
                } else {
                    binding.tvSelectedCategories.text = "Brak wybranych kategorii"
                }
            }
        }
    }
    
    private fun setupListeners() {
        binding.btnSignOut.setOnClickListener {
            lifecycleScope.launch {
                viewModel.signOut()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
        }
    }
    
    private fun loadUserData() {
        auth.currentUser?.let { user ->
            viewModel.loadUserData(user.uid)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
