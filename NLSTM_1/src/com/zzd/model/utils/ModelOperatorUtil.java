package com.zzd.model.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelOperatorUtil {
	/**
	 * shuffle index
	 * @param len : length of index
	 * @param isShuffle : is shuffle or not
	 * @return
	 */
	public static int[] shuffleIndex(int len, boolean isShuffle){
		int[] index=new int[len];
		List<Integer> list=new ArrayList<>();
		for(int i=0;i<len;i++){
			list.add(i);
		}
		if(isShuffle){
			Collections.shuffle(list);			
		}
		for(int i=0;i<len;i++){
			index[i]=(int)list.get(i);
		}		
		return index;
	}
}
