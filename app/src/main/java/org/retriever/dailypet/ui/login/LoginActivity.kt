/** @author Sehun Ahn **/

package org.retriever.dailypet.ui.login

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import org.retriever.dailypet.databinding.ActivityLoginBinding
import org.retriever.dailypet.ui.base.BaseActivity

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>({ ActivityLoginBinding.inflate(it) }) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}
