package com.mauricifj.binquery_sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.com.braspag.cieloecommerceoauth.model.AccessToken
import br.com.braspag.cieloecommerceoauth.network.HttpCredentialsClient
import br.com.braspag.cieloecommerceoauth.network.Environment as CredentialsEnvironment
import br.com.cielo.cielobinquery.CieloBinQuery
import br.com.cielo.cielobinquery.CieloBinQueryResponse
import br.com.cielo.cielobinquery.Environment as BinQueryEnvironment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val credentialsClient = HttpCredentialsClient(
            CredentialsEnvironment.SANDBOX,
                "<CLIENT-ID>",
                "<CLIENT-SECRET>"
            )

        query_button.setOnClickListener {
            setLoading()

            credentialsClient.getOAuthCredentials(
                onSuccessCallback = onSuccessCredentials,
                onError = onErrorCredentials
            )
        }
    }

    private val onSuccessCredentials: (accessToken: AccessToken) -> Unit = {
        val token = it.token
        val merchantId = "<MERCHANT-ID>"
        val cardBin = cardbin.text.toString()

        val queryService = CieloBinQuery(merchantId, BinQueryEnvironment.SANDBOX)

        queryService.query(
            bin = cardBin,
            token = token,
            onSuccessCallback = onSuccessBinQuery,
            onError = onErrorBinQuery
        )
    }

    private val onErrorCredentials: (errorMessage: String) -> Unit = {
        setLoading(false)
        showError(it)
    }

    private fun setLoading(loading: Boolean = true) {
        if (loading) {
            error_content.visibility = View.INVISIBLE
            result_content.visibility = View.INVISIBLE
            progress_content.visibility = View.VISIBLE
        }
        else
            progress_content.visibility = View.INVISIBLE
    }

    private val onSuccessBinQuery: (CieloBinQueryResponse) -> Unit = {
        setLoading(false)
        showResult(it)
    }

    private fun showResult(result: CieloBinQueryResponse) {
        status.text = result.status
        provider.text = result.provider
        cardtype.text = result.cardType
        foreigncard.text = result.foreignCard.toString()
        corporatecard.text = result.corporateCard.toString()
        issuer.text = result.issuer
        issuercode.text = result.issuerCode

        result_content.visibility = View.VISIBLE
    }

    private val onErrorBinQuery: (errorMessage: String) -> Unit = {
        setLoading(false)
        showError(it)
    }

    private fun showError(error: String) {
        error_message.text = error
        error_content.visibility = View.VISIBLE
    }
}
