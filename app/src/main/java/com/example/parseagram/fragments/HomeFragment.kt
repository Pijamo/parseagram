package com.example.parseagram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.parseagram.MainActivity
import com.example.parseagram.Post
import com.example.parseagram.PostAdapter
import com.example.parseagram.R
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery


open class HomeFragment : Fragment() {

    lateinit var postsRecyclerView: RecyclerView

    lateinit var swipeContainer: SwipeRefreshLayout

    lateinit var adapter: PostAdapter

    val allPosts: MutableList<Post> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //this is where we set up our views and click listeners

        swipeContainer = view.findViewById(R.id.swipeContainer)

        postsRecyclerView= view.findViewById(R.id.rvPost)


        //Steps to populate RecyclerView
        //1. Create layout for each row in list
        //2. Create data source for each row (This is the Post class)
        //3. Create adapter that will bridge data and row layout
        //4. Set adapter on RecyclerView
        //5. Set layout manager on RecyclerView

        swipeContainer.setOnRefreshListener {
            Log.i(TAG, "Refreshing timeline")

            allPosts.clear()

            adapter.notifyDataSetChanged()

            queryPosts()

            swipeContainer.isRefreshing = false
        }

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light)

        adapter = PostAdapter(requireContext(), allPosts)
        postsRecyclerView.adapter = adapter

        postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        queryPosts()
    }

    //query for all posts in our server
    open fun queryPosts(){
        //specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        query.include(Post.KEY_USER)
        //Find all post objects
        query.addDescendingOrder("createdAt")
        query.findInBackground(object: FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, e: ParseException?){
                if(e != null){
                    //something went wrong
                    Log.e(TAG, "Error fetching posts")
                }else{
                    if(posts != null){
                        for(post in posts){
                            Log.i(TAG, "Post: " + post.getDescription() + " , username: " + post.getUser()?.username)
                        }

                        if(posts.size > 20){
                            allPosts.addAll(posts.subList(0, 20))
                        } else {
                            allPosts.addAll(posts)
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    companion object {
        const val TAG = "HomeFragment"
    }
}