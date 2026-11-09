package com.example.devmanagerweb.data.remote

import com.example.devmanagerweb.domain.model.*
import retrofit2.http.*

interface DevManagerApi {
    @POST("auth/login")
    suspend fun login(@Body request: Map<String, String>): Usuario

    @POST("auth/register")
    suspend fun register(@Body request: Map<String, String>): Usuario

    @GET("usuarios")
    suspend fun getUsuarios(): List<Usuario>

    @GET("clientes")
    suspend fun getClientes(): List<Cliente>

    @POST("clientes")
    suspend fun addCliente(@Body cliente: Cliente)

    @PUT("clientes/{id}")
    suspend fun updateCliente(@Path("id") id: String, @Body cliente: Cliente)

    @GET("projetos")
    suspend fun getProjetos(): List<Projeto>

    @PUT("projetos/{id}")
    suspend fun updateProjeto(@Path("id") id: String, @Body projeto: Projeto)

    @GET("orcamentos")
    suspend fun getOrcamentos(): List<Orcamento>

    @PUT("orcamentos/{id}")
    suspend fun updateOrcamento(@Path("id") id: String, @Body orcamento: Orcamento)

    @GET("faturas")
    suspend fun getFaturas(): List<Fatura>

    @PUT("faturas/{id}")
    suspend fun updateFatura(@Path("id") id: String, @Body fatura: Fatura)
}
