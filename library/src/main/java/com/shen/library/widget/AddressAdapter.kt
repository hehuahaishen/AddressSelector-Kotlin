package com.shen.library.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shen.library.R
import com.shen.library.bean.City
import com.shen.library.bean.County
import com.shen.library.bean.Province
import com.shen.library.bean.Street
import java.lang.RuntimeException

/**
 * author:  shen
 * date:    2019/12/18
 *
 */
/**
 * 省份的adapter
 * 城市的adaoter
 * 乡镇的adapter
 * 街道的adaoter
 */
class  AddressAdapter<T>(var listBean : MutableList<T>) :
    RecyclerView.Adapter<AddressAdapter.AddressVH>() {

    var mIndex = INDEX_INVALID

    companion object{
        val INDEX_INVALID = -1
    }


    var mOnItemClickListener : OnItemClickListener? = null
    override fun getItemCount(): Int {
        return listBean.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressVH {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.vh_area, parent, false)
        return AddressVH(v)
    }


    override fun onBindViewHolder(holder: AddressVH, position: Int) {
        var bean = listBean[position]
        when(bean){
            is Province -> {
                val checked = mIndex >= 0 && listBean.size > mIndex &&
                        (listBean[mIndex] as Province).id == bean.id
                holder.tv.text = bean.name
                holder.tv.isEnabled = !checked
                holder.iv.visibility = if (checked) View.VISIBLE else View.INVISIBLE
            }
            is City -> {
                val checked = mIndex >= 0 && listBean.size > mIndex  &&
                        (listBean[mIndex] as City).id == bean.id
                holder.tv.text = bean.name
                holder.tv.isEnabled = !checked
                holder.iv.visibility = if (checked) View.VISIBLE else View.INVISIBLE
            }
            is County -> {
                val checked = mIndex >= 0 && listBean.size > mIndex  &&
                        (listBean[mIndex] as County).id == bean.id
                holder.tv.text = bean.name
                holder.tv.isEnabled = !checked
                holder.iv.visibility = if (checked) View.VISIBLE else View.INVISIBLE
            }
            is Street -> {
                val checked = mIndex >= 0 && listBean.size > mIndex  &&
                        (listBean[mIndex] as Street).id == bean.id
                holder.tv.text = bean.name
                holder.tv.isEnabled = !checked
                holder.iv.visibility = if (checked) View.VISIBLE else View.INVISIBLE
            }
            else -> {
                throw RuntimeException("没有这个类型")
            }
        }


        holder.itemView.setOnClickListener {
            mOnItemClickListener?.itemClick(it, position)
        }
    }

    class AddressVH(itemView : View) : RecyclerView.ViewHolder(itemView){
        val tv = itemView.findViewById<TextView>(R.id.tv_areaVH)
        val iv = itemView.findViewById<ImageView>(R.id.iv_checkMark)
    }

    fun setList(list : MutableList<T>){
        listBean.clear()
        listBean.addAll(list)
        notifyDataSetChanged()
    }

    fun cleanList(){
        listBean.clear()
        mIndex = INDEX_INVALID
        notifyDataSetChanged()
    }

    fun getSelectItem() : T? {
        if(mIndex >= 0 && listBean.size > mIndex){
            return listBean[mIndex]
        }
        return null
    }

    infix fun setSelect(index : Int){
        mIndex = index
        notifyDataSetChanged()

        if(index >= 0 && listBean.size > index){

        }
    }

    fun isOutOfBounds() : Boolean{
        return !(mIndex >= 0 && listBean.size > mIndex)
    }

    interface OnItemClickListener {
        fun itemClick(view : View, position : Int)
    }

}