package com.mybank.service.impl;

import com.mybank.api.request.dto.merchant.MerchantCreate;
import com.mybank.api.request.dto.merchant.MerchantModify;
import com.mybank.api.request.dto.merchant.MerchantQuery;
import com.mybank.api.response.dto.merchant.MerchantModifyResponse;
import com.mybank.api.response.dto.merchant.MerchantQueryResponse;
import com.mybank.aspect.ThreadLocalParams;
import com.mybank.aspect.annotation.KfApiMethodCode;
import com.mybank.base.entity.*;
import com.mybank.base.entity.constant.MerchantStatus;
import com.mybank.base.repository.*;
import com.mybank.config.FileBean;
import com.mybank.exception.CommonException;
import com.mybank.service.MerchantService;
import com.mybank.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 功能描述: 网商kf接口测试<br/>
 *
 * @author Qiang Yang(qiaoye.zqy@mybank.cn)
 * @version V1.0
 * @since 2019/4/19
 */
@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private MerchantAccountRepository accountRepository;

    @Autowired
    private MerchantContactRepository contactRepository;

    @Autowired
    private CountryAreaRepository countryAreaRepository;

    @Autowired
    private MerchantAlipayParamsRepository alipayParamsRepository;

    @Autowired
    private FileBean fileBean;

    @Value("${kf_merchant_image_cert}")
    private String certImagePath;

    @Value("${kf_merchant_image_legal_cert}")
    private String legalCertImagePath;

    @Value("${base_file_tmp_path}")
    private String tmpPath;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Merchant create(MerchantCreate dto) {
        createValidate(dto);//校验关联数据
        Merchant merchant = new Merchant();
        merchant.setName(getNonNullParams(dto.getName()));
        merchant.setId(merchant.nextId());
        merchant.setAliasName(getNonNullParams(dto.getAliasName()));
        merchant.setCertNo(getNonNullParams(dto.getCertNo()));
        merchant.setCertType(getNonNullParams(dto.getCertType()));

        String curWeek = DateUtil.format("yyyyww");

        String fileName = fileBean.copyFileToProductDir(dto.getCertImage(), KfApiMethodCode.KF_MERCHANT_CREATE.code(), "certImage", tmpPath, certImagePath);
        if (fileName != null) {
            merchant.setCertImage(curWeek + "/" + fileName);
        }
        merchant.setLegalName(dto.getLegalName());
        merchant.setLegalCertNo(dto.getLegalCertNo());
        fileName = fileBean.copyFileToProductDir(dto.getLegalCertFrontImage(), KfApiMethodCode.KF_MERCHANT_CREATE.code(), "certLegalCertFrontImage", tmpPath, legalCertImagePath);
        if (fileName != null) {
            merchant.setLegalCertFrontImage(curWeek + "/" + fileName);
        }
        fileName = fileBean.copyFileToProductDir(dto.getLegalCertBackImage(), KfApiMethodCode.KF_MERCHANT_CREATE.code(), "certLegalCertBackImage", tmpPath, legalCertImagePath);
        if (fileName != null) {
            merchant.setLegalCertBackImage(curWeek + "/" + fileName);
        }
        fileName = fileBean.copyFileToProductDir(dto.getOutDoorImages(), KfApiMethodCode.KF_MERCHANT_CREATE.code(), "outDoorImage", tmpPath, certImagePath);
        if (fileName != null) {
            merchant.setOutDoorImages(curWeek + "/" + fileName);
        }
        merchant.setMcc(getNonNullParams(dto.getMcc()));
        merchant.setProvinceCode(getNonNullParams(dto.getProvinceCode()));
        merchant.setCityCode(getNonNullParams(dto.getCityCode()));
        merchant.setDistrictCode(getNonNullParams(dto.getDistrictCode()));
        merchant.setPerMerchantCode(dto.getPerMerchantCode());
        merchant.setAddress(getNonNullParams(dto.getAddress()));
        merchant.setStatus(MerchantStatus.AUDITING.getCode());
        merchant.setNotifyUrl(getNonNullParams(dto.getNotifyUrl()));
        merchant.setPerMerchantCode(ThreadLocalParams.getInstance().getApp().getMerchantCode());
        merchant.setType(getNonNullParams(dto.getType()));
        merchant.setAppId(ThreadLocalParams.getInstance().getApp().getAppId());
        merchant.setCreateTime(ThreadLocalParams.getInstance().getCurrentDate());
        merchant.setService(getNonNullParams(dto.getService()));
        merchant.setServicePhone(getNonNullParams(dto.getServicePhone()));
        if(StringUtils.isNotBlank(dto.getThirdMerchantId())){
            Merchant mm = merchantRepository.findByThirdMerchantIdAndAppId(dto.getThirdMerchantId(), ThreadLocalParams.getInstance().getApp().getAppId());
            if(mm != null){
                throw new CommonException("该应用id(app_id)中第三方商户编号(third_merchant_id)已存在！");
            }
        }
        merchant.setThirdMerchantId(getNonNullParams(dto.getThirdMerchantId()));

        merchantRepository.save(merchant);
        createMerchantAccount(dto, merchant.getId());
        createMerchantContact(dto, merchant.getId());
        createMerchantAlipayParams(dto, merchant.getId());
        return merchant;
    }

    private void createValidate(MerchantCreate dto) {
        //判断商户名称是否存在
        Merchant merchant = merchantRepository.findTopByNameAndAppId(dto.getName(), ThreadLocalParams.getInstance().getApp().getAppId());
        if (merchant != null) {
            throw new CommonException("商户名称已存在！");
        }

        //判断省市区是否匹配
        areaValidate(dto.getDistrictCode(), dto.getCityCode(), dto.getProvinceCode());

        //判断上级商户是否存在
        perMerchantValidate(dto.getPerMerchantCode());
    }

    private void createMerchantContact(MerchantCreate dto, long merchantCode) {
        MerchantContact contact = new MerchantContact();
        contact.setMerchantCode(merchantCode);
        contact.setName(getNonNullParams(dto.getContactName()));
        contact.setPhone(getNonNullParams(dto.getContactPhone()));
        contact.setMobile(getNonNullParams(dto.getContactMobile()));
        contact.setEmail(getNonNullParams(dto.getContactEmail()));
        contact.setTag(getNonNullParams(dto.getContactTag()));
        contact.setType(getNonNullParams(dto.getContactType()));
        contact.setIdCardNo(getNonNullParams(dto.getContactIdCardNo()));
        contactRepository.save(contact);
    }

    private void createMerchantAccount(MerchantCreate dto, long merchantCode) {
        MerchantAccount account = new MerchantAccount();
        account.setMerchantCode(merchantCode);
        account.setAccountHolderName(getNonNullParams(dto.getAccountHolderName()));
        account.setAccountNo(getNonNullParams(dto.getAccountNo()));
        account.setAccountInstProvince(getNonNullParams(dto.getAccountInstProvince()));
        account.setAccountInstCity(getNonNullParams(dto.getAccountInstCity()));
        account.setAccountBranchName(getNonNullParams(dto.getAccountBranchName()));
        account.setUsageType(getNonNullParams(dto.getUsageType()));
        account.setAccountType(getNonNullParams(dto.getAccountType()));
        account.setAccountInstName(getNonNullParams(dto.getAccountInstName()));
        account.setAccountInstId(getNonNullParams(dto.getAccountInstId()));
        accountRepository.save(account);
    }

    private void createMerchantAlipayParams(MerchantCreate dto, long merchantCode) {
        MerchantAlipayParams params = new MerchantAlipayParams();
        params.setAlipayLogonId(getNonNullParams(dto.getAlipayLogonId()));
        params.setAlipayUserId(getNonNullParams(dto.getAlipayUserId()));
        params.setMerchantCode(merchantCode);
        alipayParamsRepository.save(params);
    }

    @Override
    @Transactional
    public MerchantQueryResponse query(MerchantQuery dto) {
        if (dto.getMerchantCode() == null
                && StringUtils.isBlank(dto.getThirdMerchantId())) {
            throw new CommonException("商户编号和第三方商户编号不能同时为空！");
        }

        Merchant merchant = null;
        if(dto.getMerchantCode() != null) {
            merchant = merchantRepository.findById(dto.getMerchantCode()).orElse(null);
        }
        if (merchant == null && StringUtils.isNotBlank(dto.getThirdMerchantId())) {
            merchant = merchantRepository.findByThirdMerchantIdAndAppId(dto.getThirdMerchantId(), ThreadLocalParams.getInstance().getApp().getAppId());
        }

        if (merchant == null || !Objects.equals(ThreadLocalParams.getInstance().getApp().getAppId(), merchant.getAppId())) {
            throw new CommonException("商户不存在！");
        }

        MerchantQueryResponse response = new MerchantQueryResponse();

        response.setName(merchant.getName());
        response.setAliasName(merchant.getAliasName());
        response.setCertType(merchant.getCertType());
        response.setCertNo(merchant.getCertNo());
        response.setMcc(merchant.getMcc());
        response.setType(merchant.getType());
        response.setLegalName(merchant.getLegalName());
        response.setLegalCertNo(merchant.getLegalCertNo());
        response.setProvinceCode(merchant.getProvinceCode());
        response.setCityCode(merchant.getCityCode());
        response.setDistrictCode(merchant.getDistrictCode());
        response.setAddress(merchant.getAddress());
        response.setPerMerchantCode(merchant.getPerMerchantCode());
        response.setCreateTime(merchant.getCreateTime());
        response.setService(merchant.getService());
        response.setServicePhone(merchant.getServicePhone());
        response.setThirdMerchantId(merchant.getThirdMerchantId());
        response.setRemark(merchant.getRemark());
        response.setStatus(merchant.getStatus());
        MerchantAccount account = accountRepository.findById(merchant.getId()).orElse(null);
        if (account != null) {
            response.setAccountHolderName(account.getAccountHolderName());
            response.setAccountNo(account.getAccountNo());
            response.setAccountInstProvince(account.getAccountInstProvince());
            response.setAccountInstCity(account.getAccountInstCity());
            response.setAccountBranchName(account.getAccountBranchName());
            response.setUsageType(account.getUsageType());
            response.setAccountType(account.getAccountType());
            response.setAccountInstName(account.getAccountInstName());
            response.setAccountInstId(account.getAccountInstId());
        }

        MerchantContact contact = contactRepository.findById(merchant.getId()).orElse(null);
        if (contact != null) {
            response.setContactName(contact.getName());
            response.setContactPhone(contact.getPhone());
            response.setContactMobile(contact.getMobile());
            response.setContactEmail(contact.getEmail());
            response.setContactTag(contact.getTag());
            response.setContactType(contact.getType());
            response.setContactIdCardNo(contact.getIdCardNo());
        }

        MerchantAlipayParams alipayParams = alipayParamsRepository.findById(merchant.getId()).orElse(null);
        if (alipayParams != null) {
            response.setAlipayLogonId(alipayParams.getAlipayLogonId());
            response.setAlipayUserId(alipayParams.getAlipayUserId());
        }

        return response;
    }

    @Override
    @Transactional
    public MerchantModifyResponse modify(MerchantModify dto) {
        Merchant merchant = modifyValidate(dto);
        merchant.setName(getNonNullParams(dto.getName(), merchant.getName()));
        merchant.setAliasName(getNonNullParams(dto.getAliasName(), merchant.getAliasName()));
        merchant.setCertNo(getNonNullParams(dto.getCertNo(), merchant.getCertNo()));
        merchant.setCertType(getNonNullParams(dto.getCertType(), merchant.getCertType()));
        String fileName = fileBean.copyFileToProductDir(dto.getCertImage(), KfApiMethodCode.KF_MERCHANT_MODIFY.code(), "certImage", tmpPath, certImagePath);
        if (fileName != null) {
            merchant.setCertImage(fileName);
        }
        merchant.setLegalName(getNonNullParams(dto.getLegalName(), merchant.getLegalName()));
        merchant.setLegalCertNo(getNonNullParams(dto.getLegalCertNo(), merchant.getLegalCertNo()));
        fileName = fileBean.copyFileToProductDir(dto.getLegalCertFrontImage(), KfApiMethodCode.KF_MERCHANT_MODIFY.code(), "certLegalCertFrontImage", tmpPath, legalCertImagePath);
        if (fileName != null) {
            merchant.setLegalCertFrontImage(fileName);
        }
        fileName = fileBean.copyFileToProductDir(dto.getLegalCertBackImage(), KfApiMethodCode.KF_MERCHANT_MODIFY.code(), "certLegalCertBackImage", tmpPath, legalCertImagePath);
        if (fileName != null) {
            merchant.setLegalCertBackImage(fileName);
        }
        fileName = fileBean.copyFileToProductDir(dto.getOutDoorImages(), KfApiMethodCode.KF_MERCHANT_MODIFY.code(), "outDoorImage", tmpPath, certImagePath);
        if (fileName != null) {
            merchant.setOutDoorImages(fileName);
        }
        merchant.setMcc(getNonNullParams(dto.getMcc(), merchant.getMcc()));
        merchant.setProvinceCode(getNonNullParams(dto.getProvinceCode(), merchant.getProvinceCode()));
        merchant.setCityCode(getNonNullParams(dto.getCityCode(), merchant.getCityCode()));
        merchant.setDistrictCode(getNonNullParams(dto.getDistrictCode(), merchant.getDistrictCode()));
        merchant.setPerMerchantCode(dto.getPerMerchantCode() == null ? merchant.getPerMerchantCode() : dto.getPerMerchantCode());
        merchant.setAddress(getNonNullParams(dto.getAddress(), merchant.getAddress()));
        merchant.setNotifyUrl(getNonNullParams(dto.getNotifyUrl(), merchant.getNotifyUrl()));
        merchant.setType(getNonNullParams(dto.getType(), merchant.getType()));
        merchant.setAppId(ThreadLocalParams.getInstance().getApp().getAppId());
        merchant.setCreateTime(ThreadLocalParams.getInstance().getCurrentDate());
        merchant.setService(getNonNullParams(dto.getService(), merchant.getService()));
        merchant.setServicePhone(getNonNullParams(dto.getServicePhone(), merchant.getServicePhone()));
        merchant.setPerMerchantCode(ThreadLocalParams.getInstance().getApp().getMerchantCode());
        MerchantModifyResponse response = create(merchant);
        modifyMerchantAccount(dto, merchant.getId());
        modifyMerchantContact(dto, merchant.getId());
        modifyMerchantAlipayParams(dto, merchant);

        merchantRepository.saveAndFlush(merchant);

        return response;
    }

    private void modifyMerchantAccount(MerchantModify dto, Long merchantCode) {
        MerchantAccount account = accountRepository.findById(merchantCode).orElse(new MerchantAccount());
        account.setMerchantCode(merchantCode);
        account.setAccountHolderName(getNonNullParams(dto.getAccountHolderName(), account.getAccountHolderName()));
        account.setAccountNo(getNonNullParams(dto.getAccountNo(), account.getAccountNo()));
        account.setAccountInstProvince(getNonNullParams(dto.getAccountInstProvince(), account.getAccountInstProvince()));
        account.setAccountInstCity(getNonNullParams(dto.getAccountInstCity(), account.getAccountInstCity()));
        account.setAccountBranchName(getNonNullParams(dto.getAccountBranchName(), account.getAccountBranchName()));
        account.setUsageType(getNonNullParams(dto.getUsageType(), account.getUsageType()));
        account.setAccountType(getNonNullParams(dto.getAccountType(), account.getAccountType()));
        account.setAccountInstName(getNonNullParams(dto.getAccountInstName(), account.getAccountInstName()));
        account.setAccountInstId(getNonNullParams(dto.getAccountInstId(), account.getAccountInstId()));
        accountRepository.save(account);
    }

    private void modifyMerchantContact(MerchantModify dto, Long merchantCode) {
        MerchantContact contact = contactRepository.findById(merchantCode).orElse(new MerchantContact());
        contact.setMerchantCode(merchantCode);
        contact.setName(getNonNullParams(dto.getContactName(), contact.getName()));
        contact.setPhone(getNonNullParams(dto.getContactPhone(), contact.getPhone()));
        contact.setMobile(getNonNullParams(dto.getContactMobile(), contact.getMobile()));
        contact.setEmail(getNonNullParams(dto.getContactEmail(), contact.getEmail()));
        contact.setTag(getNonNullParams(dto.getContactTag(), contact.getTag()));
        contact.setType(getNonNullParams(dto.getContactType(), contact.getType()));
        contact.setIdCardNo(getNonNullParams(dto.getContactIdCardNo(), contact.getIdCardNo()));
        contactRepository.save(contact);
    }

    private void modifyMerchantAlipayParams(MerchantModify dto, Merchant merchant) {
        long merchantCode = merchant.getId();
        MerchantAlipayParams params = alipayParamsRepository.findById(merchantCode).orElse(new MerchantAlipayParams());
        //alipay_logon_id || alipay_user_id 发生变化，更新商户状态为待审批
        if (!dto.getAlipayLogonId().equals(params.getAlipayLogonId()) || !dto.getAlipayUserId().equals(params.getAlipayUserId())
                || merchant.getStatus() == MerchantStatus.NOT_PASS.getCode()) {
            merchant.setStatus(MerchantStatus.AUDITING.getCode());
        }
        params.setAlipayLogonId(getNonNullParams(dto.getAlipayLogonId(), params.getAlipayLogonId()));
        params.setAlipayUserId(getNonNullParams(dto.getAlipayUserId(), params.getAlipayUserId()));
        params.setMerchantCode(merchantCode);
        alipayParamsRepository.save(params);
    }

    private MerchantModifyResponse create(Merchant merchant) {
        MerchantModifyResponse response = new MerchantModifyResponse();
        response.setName(merchant.getName());
        response.setAliasName(merchant.getAliasName());
        response.setCertType(merchant.getCertType());
        response.setCertNo(merchant.getCertNo());
        response.setMcc(merchant.getMcc());
        response.setMerchantCode(merchant.getId());
        response.setType(merchant.getType());
        response.setStatus(merchant.getStatus());
        response.setLegalName(merchant.getLegalName());
        response.setLegalCertNo(merchant.getLegalCertNo());
        response.setProvinceCode(merchant.getProvinceCode());
        response.setCityCode(merchant.getCityCode());
        response.setDistrictCode(merchant.getDistrictCode());
        response.setAddress(merchant.getAddress());
        response.setThirdMerchantId(merchant.getThirdMerchantId());
        return response;
    }

    private Merchant modifyValidate(MerchantModify dto) {
        if (dto.getMerchantCode() == null
                && StringUtils.isBlank(dto.getThirdMerchantId())) {
            throw new CommonException("商户编号和第三方商户编号不能同时为空！");
        }

        Merchant merchant = null;
        if(dto.getMerchantCode() != null) {
            merchant = merchantRepository.findById(dto.getMerchantCode()).orElse(null);
        }

        if (merchant == null && StringUtils.isNotBlank(dto.getThirdMerchantId())) {
            merchant = merchantRepository.findByThirdMerchantIdAndAppId(dto.getThirdMerchantId(), ThreadLocalParams.getInstance().getApp().getAppId());
        }

        if (merchant == null || !Objects.equals(ThreadLocalParams.getInstance().getApp().getAppId(), merchant.getAppId())) {
            throw new CommonException("商户不存在！");
        }
        int count = merchantRepository.countAllByNameAndIdNotEqualAnAndAppId(dto.getName(), merchant.getId(), ThreadLocalParams.getInstance().getApp().getAppId());
        if (count != 0) {
            throw new CommonException("商户名已存在！");
        }
        //判断省市区是否匹配
        areaValidate(dto.getDistrictCode(), dto.getCityCode(), dto.getProvinceCode());

        //判断上级商户是否存在
        perMerchantValidate(dto.getPerMerchantCode());

        return merchant;
    }

    /**
     * 判断上级商户是否存在
     *
     * @param perMerchantCode 上级商户编号
     */
    private void perMerchantValidate(Long perMerchantCode) {
        if (Objects.nonNull(perMerchantCode)) {
            Merchant parent = merchantRepository.findById(perMerchantCode).orElse(null);
            if (parent == null || !Objects.equals(ThreadLocalParams.getInstance().getApp().getAppId(), parent.getAppId())) {
                throw new CommonException("上级商户不存在！");
            }
        }
    }

    /**
     * 判断省市区是否匹配
     *
     * @param districtCode 区编号
     * @param cityCode     市编号
     * @param provinceCode 省编号
     */
    private void areaValidate(String districtCode, String cityCode, String provinceCode) {

        CountryArea area = countryAreaRepository.findById(districtCode).orElse(null);
        if (area == null) {
            throw new CommonException("区编码不存在！");
        } else {
            StringBuilder sb = new StringBuilder(area.getCode());
            while (area.getParent() != null) {
                sb.append("|");
                area = area.getParent();
                sb.append(area.getCode());
            }
            if (!sb.toString().equals(districtCode + "|" + cityCode + "|" + provinceCode)) {
                throw new CommonException("省市区编码不匹配！");
            }
        }
    }

    private String getNonNullParams(String newVal) {
        return getNonNullParams(newVal, null);
    }

    private String getNonNullParams(String newVal, String oldVal) {
        if (StringUtils.isBlank(newVal)) {
            if (StringUtils.isNotBlank(oldVal)) {
                return oldVal;
            } else {
                return "";
            }
        } else {
            return newVal;
        }
    }
}
