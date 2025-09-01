package com.instamedia.convertify.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.instamedia.convertify.R
import com.instamedia.convertify.models.VideoFile
import java.util.ArrayList

class FilesAdapter(
    private val context: Context,
    private val onItemClick: OnItemClickListener? = null,
    private val onMoreClick: OnMoreClickListener? = null
) : RecyclerView.Adapter<FilesAdapter.FileViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(videoFile: VideoFile)
    }

    interface OnMoreClickListener {
        fun onMoreClick(videoFile: VideoFile)
    }

    private var videoFiles: ArrayList<VideoFile> = ArrayList()
    private var filteredFiles: ArrayList<VideoFile> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_file, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val videoFile = filteredFiles[position]
        holder.bind(videoFile)
    }

    override fun getItemCount(): Int {
        return filteredFiles.size
    }

    fun updateFiles(newFiles: List<VideoFile>) {
        videoFiles.clear()
        videoFiles.addAll(newFiles)
        filteredFiles.clear()
        filteredFiles.addAll(newFiles)
        notifyDataSetChanged()
    }

    fun filterFiles(query: String) {
        filteredFiles.clear()

        if (query.length == 0) {
            filteredFiles.addAll(videoFiles)
        } else {
            for (file in videoFiles) {
                val queryLower = query.toLowerCase(java.util.Locale.getDefault())
                val titleLower = file.title.toLowerCase(java.util.Locale.getDefault())
                val authorLower = file.author.toLowerCase(java.util.Locale.getDefault())

                val titleContains = titleLower.indexOf(queryLower) != -1
                val authorContains = authorLower.indexOf(queryLower) != -1

                if (titleContains || authorContains) {
                    filteredFiles.add(file)
                }
            }
        }
        notifyDataSetChanged()
    }

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val videoPlatform: ImageView
        private val videoTitle: TextView
        private val videoAuthor: TextView
        private val videoDate: TextView
        private val videoActions: ImageView

        init {
            videoPlatform = itemView.findViewById(R.id.videoPlatform)
            videoTitle = itemView.findViewById(R.id.videoTitle)
            videoAuthor = itemView.findViewById(R.id.videoAuthor)
            videoDate = itemView.findViewById(R.id.videoDate)
            videoActions = itemView.findViewById(R.id.videoActions)
        }

        fun bind(videoFile: VideoFile) {
            // Configurar imagen de plataforma
            val platformIconRes = when (videoFile.platform) {
                VideoFile.Platform.TIKTOK -> R.drawable.tiktok_icon
                VideoFile.Platform.YOUTUBE -> R.drawable.youtube_icon
                VideoFile.Platform.INSTAGRAM -> R.drawable.instagram_icon
                VideoFile.Platform.FACEBOOK -> R.drawable.facebook_icon
                VideoFile.Platform.UNKNOWN -> R.drawable.tiktok_icon
            }
            videoPlatform.setImageResource(platformIconRes)

            // Configurar textos
            videoTitle.text = videoFile.title
            videoAuthor.text = videoFile.author
            videoDate.text = videoFile.downloadDate

            // Configurar click listeners
            itemView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    if (onItemClick != null) {
                        onItemClick.onItemClick(videoFile)
                    }
                }
            })

            videoActions.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    if (onMoreClick != null) {
                        onMoreClick.onMoreClick(videoFile)
                    }
                }
            })
        }
    }
}