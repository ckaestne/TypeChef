#if 0
#if (LINUX_VERSION_CODE >= KERNEL_VERSION(2,6,20))
        INIT_WORK(&ieee->ht_onAssRsp, (void(*)(void*)) HTOnAssocRsp_wq);
#else
        INIT_WORK(&ieee->ht_onAssRsp, (void(*)(void*)) HTOnAssocRsp_wq, ieee);
#endif
#endif