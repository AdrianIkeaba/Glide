package HomeFragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fastchat.UserListAdapter
import com.example.fastchat.UserListModel
import com.example.fastchat.databinding.FragmentChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatFrag : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding
    private lateinit var database: FirebaseDatabase
    private lateinit var users: ArrayList<UserListModel>
    private lateinit var userAdapter: UserListAdapter
    private lateinit var user: UserListModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        database = FirebaseDatabase.getInstance()
        users = ArrayList()
        userAdapter = UserListAdapter(requireContext(), users)

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        _binding!!.recycleView.layoutManager = layoutManager
        database.reference.child("users")
            .child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(UserListModel::class.java)!!
                }

                override fun onCancelled(error: DatabaseError) { }
            })
        _binding!!.recycleView.adapter = userAdapter
        database.reference.child("users").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                users.clear()
                for (snapshot1 in snapshot.children) {
                    val user: UserListModel = snapshot1.getValue(UserListModel::class.java)!!
                    if (user.uid!! != FirebaseAuth.getInstance().uid) users.add(user)
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) { }

        })
        // Inflate the layout for this fragment
        return binding!!.root


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        database.reference.child("presence")
            .child(currentId!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        database.reference.child("presence")
            .child(currentId!!).setValue("Offline")
    }
}