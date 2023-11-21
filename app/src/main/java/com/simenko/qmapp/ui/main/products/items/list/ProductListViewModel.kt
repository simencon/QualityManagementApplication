package com.simenko.qmapp.ui.main.products.items.list

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.di.ProductKindIdParameter
import com.simenko.qmapp.domain.ID
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    @ProductKindIdParameter productKindId: ID
): ViewModel() {

}